//Tomasz Koczar-5

import java.util.Scanner;

/*Idea rozwiazania:
    Konwersja z INF do ONP odbywa sie zgodnie 
    z idea algorytmu opisana w matetiale do 5 wykladu o stosach,listach i kolejkach.
    Piorytet nawiasow zostal natomiast zmieniony na najnizszy co stoi w przeciwnosci z 
    sugestia w tresci zadania. Powod tego jest nastepujacy: przy siaganiu wszystich 
    operatorow o wiekszym/nieminiejszym piorytecie ze stosu nawias lewostorny rowniez
    zostawal scigany co puslo dzialanie algorytmu. 

    Automat sprawdzajacy zostal zaimplemnetowany w osobnej klasie.

    Warunek sprawdzania 2b jest nie potrzebny poniewaz kazda wyrazenie zawierajce
    nie wlasciwa ilos operatorow w stosunku do operandow zostanie wychwycone.

    Konwersja z ONP do INF odbywa sie tak jak bysmy to zrobili na kartce papieru.
    */

/*Test dolaczny do tresci zadania zwraca:
INF: x = a * ( b * c ) 
INF: a + b + ( ~ a - a ) 
ONP: x a ~ ~ b c * + = 
ONP: t a ~ ~ b c * + = 
ONP: a b + c / 
INF: a * ( b - c ) 
INF: error
ONP: x a b c d e ^ ^ = = = 
ONP: r y + a b c + d + = = 
ONP: x c a > c b < & ! = 
ONP: x a ~ ~ ~ = 
INF: x = ~ ~ ~ a 
Co jest zgodne z wyjciem oczekiwanym.
 */

/**Klasa implementujaca strukture dancyh zwana stosem*/
class Stack{
    /*--------Pola--------*/
    static private int MAX_SIZE = 256; // maksymalny rozmiar stosu
    private int currentSize; // obecny rozmiar stosu
    private char[] stack; // stos

    /*------Metody--------*/
    /**Konstruktor*/
    public Stack(){ stack = new char[MAX_SIZE]; currentSize=0; }

    /**Wstawia nowa wartos na szczyt stosu*/
    public void push(char elem){
        stack[currentSize++] = elem;
    }

    /**Zwraca szczyt stosu*/
    public char top(){
        return stack[currentSize-1];
    }

    /**Usuwa ze szczytu*/
    public void pop(){
        if(currentSize == 0 ){
            System.out.println("Ronisz cos zle!");
            return;
        }
        currentSize--;
    }

    /**Sprawcza czy stos jest pusty*/
    public boolean isEmpty(){
        if(currentSize==0)
            return true;
        return false;
    }

    /**Zwraca obecny rozmiar stosu */
    public int getCurrentSize(){
        return currentSize;
    }
}

/**Klasa implementujaca strukture danych zwana lista */
class List{
    /*-----------Pola-------------*/
    static private int MAX_SIZE = 256;
    private int currentSize;//Rozmiar listy
    private char[] list;
    /*-----------Metody-------------*/
    /**Konstuktor */
    List(){
        currentSize = 0;
        list = new char[MAX_SIZE];
    }
    /**Akcesor rozmiaru */
    public int getCurrentSize(){ return currentSize; }
    /**Akcesor lisy */
    public char at(int index){ return list[index]; }
    /**Dopisz na poczatek */
    public void pushFront(char c){ 
        /*Przepisz w prawo*/
        for(int i = currentSize ; i  >0; i--)
            list[i] = list[i-1];
        /*Dopisz na poczatek*/
        list[0] = c;

        /*Powieksz rozmiar*/
        currentSize++;
    }
    /*Dopisz na koniec*/
    public void pushLast(char c){
        list[currentSize++] = c;
    }
    /*Usun z konca*/
    public void removeLast(){
        currentSize--;
    }
}

/**Klasa obslugujaca piorytet operatorow */ 
class Operators{
    /*---------Pola--------------*/
    static private String operators = "()!~^*/%+-<>?&|=";
    /*------------Metody----------*/
    /**Sprawdza czy znak jest operatorem */
    public boolean isOperator(char c){
        if (operators.indexOf(c)>-1)return true;
        return false;
    }

    /**Zwraca priorytet operatora, przy blednym znaku -1 */
    public int getPiority(char c){
        if(c == '(' || c == ')') return 0;
        if(c == '!' || c == '~') return 9;
        if(c == '^') return 8;
        if(c == '*' || c == '/' || c == '%') return 7;
        if(c == '+' || c == '-') return 6;        
        if(c == '<' || c == '>') return 5;
        if(c == '?') return 4;
        if(c == '&') return 3;
        if(c == '|') return 2;
        if(c == '=') return 1;
        return -1;
    }
    /**Zwraca lacznosc operatora. 
     * 1 dla lewostronej 2 dla prawostronej */
    public int getConnectivity(char c){
        if(c == '(' || c == ')') return 1;
        if(c == '!' || c == '~') return 2;
        if(c == '^') return 2;
        if(c == '*' || c == '/' || c == '%') return 1;
        if(c == '+' || c == '-') return 1;        
        if(c == '<' || c == '>') return 1;
        if(c == '?') return 1;
        if(c == '&') return 1;
        if(c == '|') return 1;
        if(c == '=') return 2;
        return -1;
    }

    /**Sprawdza czy znak jest zmienna */
    public boolean isVariable(char c){
        if( 'a' <= c && c <= 'z' ) return true;
        return false;
    }

    /**Sprawdza czy znak jest liczba */
    public boolean isNumber(char c){
        if('0' <= c && c <='9') return true;
        return false;
    }
}

/**Klasa reprezentujaca automat badajacy poprawnosc wyrazen INF*/
class Automat{
    /*Mini klasa reprezentujaca stan automatu*/
    class Status{
        private boolean current;

        private boolean isActive(){return current; }

        private void deactivate(){ current = false; }
        private void activate() { current = true ;}
    }
    /*-----Pola-----*/
    private Status zero,one,two,trash; // zero to poczatkowy, one to akceptujacy

    /*----Metody----*/

    /**Konstruktor */
    Automat(){
        zero = new Status(); zero.activate();
        one = new Status(); one.deactivate();
        two = new Status(); two.deactivate();
        trash = new Status(); trash.deactivate();//panie. tego juz nie uratujesz.
    }


    /**Funkcja przejscia */

    public void functionForOperands(){//dla zmiennych lub liczb
        if(zero.isActive()){ zero.deactivate(); one.activate();}
        else if(one.isActive()){ trash.activate();}//z jedynki nie wychodzi strzalka z 'z'
        else{ two.deactivate(); one.activate();}
    }
    
    public void functionForO1(){//operatory jendoarkumentowe
        if(zero.isActive()){ zero.deactivate();two.activate(); }
        else if(one.isActive()){trash.activate();} // nie wychodzi o1
        else {} // wraca do siebie
    }

    public void functionForO2(){//operatory dwuargumentowe
        if(zero.isActive()){trash.activate();} // nie ma strzaleczki
        else if(one.isActive()){ one.deactivate(); zero.activate();}
        else {trash.activate();}// nie ma strzaleczki
    }

    public void functionForLeftParenthesis(){//dla '('
        if(zero.isActive()){ }//wraca do siebie
        else if(one.isActive()){ trash.activate(); }//nie ma strzaleczki
        else {two.deactivate();zero.activate();}
    }
    public void functionForRightParenthesis(){//dla '('
        if(zero.isActive()){ trash.activate();}// nie ma strzaleczki
        else if(one.isActive()){}//wraca do siebie 
        else { trash.activate(); }// nie ma strzaleczki
    }

    /**Sprawdzanie stanu */
    public int getCurrentStatus(){
        if(trash.isActive()) return -1;
        if(zero.isActive()) return 0;
        if(one.isActive()) return 1;
        if(two.isActive()) return 2;
        else return -1;
        }
}

/**Klasa obslugujaca wyrazenie np.(a+b).
 * Przyda sie w konwersji ONP->INF
 */
class Expression{
    public String operator;
    public String varLeft,varRight;
    public int getPriority(){ 
        Operators op = new Operators();
        if(operator == null) return 10;
        return op.getPiority(operator.charAt(0));
    }
    public int getConnectivity(){
        Operators op = new Operators();
        return op.getConnectivity(operator.charAt(0));
    }
    public String toString(){
        String s = new String();
        if(varLeft!=null) s+=varLeft;
        if(operator!=null) s+=operator;
        if(varRight!=null) s+=varRight;
        return s;
    }
    public void addParnet(){
        String s = "(";
        varLeft=s+varLeft;
        varRight+=")";
    }
    public Expression(String left, String o , String right){
        varLeft = left;
        operator = o;
        varRight = right;
    }
    public Expression( String o,String right){
        operator = o;
        varRight = right;
        //varLeft = null;
    }
    public Expression(String var){
        varLeft = var;
        //varRight = null;
        //operator = null;
    }
}

/**Stos dla Expression */
class StackExpr{
    /*--------Pola--------*/
    static private int MAX_SIZE = 256; // maksymalny rozmiar stosu
    private int currentSize; // obecny rozmiar stosu
    private Expression[] stack; // stos

    /*------Metody--------*/
    /**Konstruktor*/
    public StackExpr(){ stack = new Expression[MAX_SIZE]; currentSize=0; }

    /**Wstawia nowa wartos na szczyt stosu*/
    public void push(Expression elem){
        stack[currentSize++] = elem;
    }

    /**Zwraca szczyt stosu*/
    public Expression top(){
        return stack[currentSize-1];
    }

    /**Usuwa ze szczytu*/
    public void pop(){
        if(currentSize == 0 ){
            System.out.println("Ronisz cos zle!");
            return;
        }
        currentSize--;
    }

    /**Sprawcza czy stos jest pusty*/
    public boolean isEmpty(){
        if(currentSize==0)
            return true;
        return false;
    }

    /**Zwraca obecny rozmiar stosu */
    public int getCurrentSize(){
        return currentSize;
    }
}

/**Klasa obslugujaca zestaw danych */
class Data{
    /*---------------Pola-----------------*/
    private enum TYPE{ IFN, ONP  }; // mozliwe typy danych;
    private TYPE type; //typ zestawu
    private List input,output;//listy potrzebne do konwersji
    private Stack stack; // stos potrzbny do konwersji
    private boolean correct;//poprawnosc danych
    /*--------------Metody----------------*/

    /**Konstruktor */
    Data(String string){
        //System.out.println("Zaczynam konstuowac");
        String[] split = string.split(":");
        if(split[0].equals("INF")) type = TYPE.IFN;
        else if(split[0].equals("ONP")) type = TYPE.ONP;

        input = new List();

        for(int i = 0; i<split[1].length(); i++){
            input.pushLast(split[1].charAt(i));
        }

        output = new List();
        stack = new Stack();
        correct = true;
        //System.out.println("Koncze konstuowac");
    }

    /*Akcesor typu zwraca 1 dla INF i 2 dla ONP*/
    public int getType(){
        if(type == TYPE.IFN) return 1;
        else if(type == TYPE.ONP) return 2;
        return -1;
    }

    /**Konwersja oraz sprawdzanie poprawnosci*/
    public void convert(){
        Operators op = new Operators();// przydatne metody
        int piority;//przyda sie

        if(type == TYPE.IFN){//jesli type wyrazenia==inf

            Automat au = new Automat();//Automat do sprwadzania poprawnosci

            char symbol;// symbol ktorym aktulanie sie zajmujemy
            int index;// miejsce w ktorym akualnie jestesmy

            int leftParenthesis = 0;//liczba lewych '('
            int rightParenthesis = 0;//liczba prawych ')'

            //int op2 = 0; //liczba operatorow dwuargumentowych
            //int operands = 0; //liczba opernadow


            for(index = 0; index < input.getCurrentSize(); index++){//dla kazego symblou
                symbol = input.at(index);
                //System.out.println("Symbol to "+ symbol);

                if(op.isVariable(symbol)){//jesli jest liczba lub zmianna
                    au.functionForOperands();//do automatu
                    output.pushLast(symbol);//dopisz go na koniec
                    //operands++;
                }

                else if(symbol == '('){//jesli jest '('
                    au.functionForLeftParenthesis();//do automatu
                    stack.push(symbol);//na stos!!
                    leftParenthesis++;
                    //System.out.println("Pcham: "+ symbol + " " + stack.getCurrentSize());

                } 

                else if(op.isOperator(symbol)&& symbol!=')'){// jesli jest operatorem oprocz ')'
                    if(symbol == '!' || symbol == '~'){
                        au.functionForO1();//do automatu
                    }
                    else{
                        au.functionForO2();//do automatu
                        //op2++;
                    }

                    if(op.getConnectivity(symbol)==1){//lacznosc lewostrona
                        piority = op.getPiority(symbol);//pobierz priorytet
                        while(!stack.isEmpty() && op.getPiority(stack.top())>=piority){//sciagnij ze stosu i dopisz na wyjscie
                            output.pushLast(stack.top());//wszystko o piorytecie nie mniejszym
                            stack.pop();
                        }
                    }
                    else if(op.getConnectivity(symbol)==2){//lacznosc prawostrona
                        piority = op.getPiority(symbol);//pobierz piorytet
                        while(!stack.isEmpty() && op.getPiority(stack.top())>piority){//sciegonij ze stosu i dopisz na wyscie
                            output.pushLast(stack.top());//wszystko o piorytecie wiekszym
                            stack.pop();
                        }
                    }
                    else{// jesli lacznosc zwroci -1
                        System.out.println("Houston, mamy problemy z lacznoscia");
                    }
                    stack.push(symbol);//na stos!!
                }
                else if(symbol == ')'){//jesli jest ')'
                    rightParenthesis++;
                    au.functionForRightParenthesis();//do automatu

                    while(!stack.isEmpty() && stack.top()!='('){//sciagnij ze stosu i dopisz wszystko 
                        //System.out.println(stack.top());
                        output.pushLast(stack.top());// az do '('
                        stack.pop();
                        //System.out.println(stack.getCurrentSize());
                    }
                    if(!stack.isEmpty()) stack.pop();// sciagnij ze stosu '('
                }

                if(rightParenthesis>leftParenthesis) correct = false;//jesli nawias sie zamknol zanim otworzyl

            }//tu sie konczy for
            while(!stack.isEmpty()){
                output.pushLast(stack.top());
                stack.pop();
            }
            if(!(au.getCurrentStatus()==1)) correct =  false;//jesli automat nie skonczyl w 1
            if(leftParenthesis!=rightParenthesis) correct =false;
            //if(op2*2 != operands || op2*2 != operands+1) correct = false;//jesli liczba operandow nie pasuje do operatrow
        }//tu sie konczy if type == inf
        else if(type==TYPE.ONP){//jesli wyrazenie jest ONP
            char c;
            String s;
            Expression left,right;
            
            StackExpr stack = new StackExpr();
            for(int index = 0; index < input.getCurrentSize(); index++){//tu sie zaczyna for
                c = input.at(index);
                if(op.isVariable(c)){// jesli zmiena
                    s = ""+c;
                    stack.push(new Expression(s));//zamien w wyrazenie i poslij na stos
                }
                else if(op.isOperator(c)){
                    if(c == '!' || c=='~'){//jesli jedno argumentwy
                        s = ""+c;
                        if(!stack.isEmpty()){//jesli pusty to zle
                            right=stack.top();
                            stack.pop();//sciagamy ze stosu;
                        }else{
                            correct = false;
                            return;
                        }

                        if(op.getPiority(c)>right.getPriority()){//dodajemy nawiasy jesli potrzebne
                            right.addParnet();
                        }
                        stack.push(new Expression( s, right.toString()));//dodajmy wynik na stos
                    }
                    else if(c!= '(' && c!= ')'){//jesli dwu
                        if(!stack.isEmpty()){//jesli pusty to zle
                            right=stack.top();
                            stack.pop();//sciagamy ze stosu;
                        }else{
                            correct = false;
                            return;
                        }
                        if(!stack.isEmpty()){// jesli pusty to zle
                            left=stack.top();
                            stack.pop();//sciagamy ze stosu;
                        }else{
                            correct = false;
                            return;
                        }
                        
                        if(op.getPiority(c) > left.getPriority() && !left.operator.equals("^")) left.addParnet();// w lewym musi byc wiekszy bo tak kaza
                        if(op.getPiority(c) >= right.getPriority() && !right.operator.equals("^")) {right.addParnet();}
                        
                        s= ""+c;
                        stack.push(new Expression(left.toString(), s, right.toString()));
                    }
                }
            }//tu sie konczy for

            s = stack.top().toString();
            if(!stack.isEmpty())
                stack.pop();//stos powinien byc teraz czysty
            else {
                correct =false;
                return;
            }

            if(!stack.isEmpty()){
                correct = false;
                return;
                }
            for(int i = 0; i<s.length(); i++)
                output.pushLast(s.charAt(i));
        }//tu sie koczy if type==onp
    }// tu sie konczy konwersja

    /**Prezentuje wynki */
    public void displayConveted(){
        if(type == TYPE.IFN) 
            System.out.print("ONP: ");
        else if(type == TYPE.ONP) 
            System.out.print("INF: ");

        if(correct){
            for(int i = 0; i<output.getCurrentSize(); i++){
                System.out.print(output.at(i) + " ");
            }
            System.out.print("\n");
        }
        else{
            System.out.println("error");
        }
    }
}




/**----------Klasa glowna programu------------*/
public class Source {

    /*Wejscie do programu*/
    public static Scanner sc = new Scanner(System.in);

    /**-----------Program-------------------*/
    public static void main(String[] args){
        int noLines = sc.nextInt();// liczba wyrazen
        sc.nextLine();
        for(int i = 0; i < noLines; i++ ){
            Data Line = new Data(sc.nextLine());//tworzy zestaw
            Line.convert();
            Line.displayConveted();
            
        }
    }


}
