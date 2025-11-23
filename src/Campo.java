import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Campo extends JPanel {

    private Cella[][] campo;

    private final int mine;
    private int nBandiere = 0;
    private final int MINA = -1;

    private boolean primoClick = true;

    public Campo(int righe, int colonne, int maxMine, Container content) {

        setPreferredSize(new Dimension(450,300));
        campo = new Cella[righe][colonne];
        this.mine = maxMine;

        // gestire input delle celle
        for (int r=0; r < campo.length; r++) {
            for (int c=0; c < campo[0].length; c++) {

                // istanzare la cella e posizionarla a schermo
                campo[r][c] = new Cella(30, r, c);
                content.add(campo[r][c]);
                campo[r][c].setLocation(campo[r][c].getWidth() * c, campo[r][c].getHeight() * r); 

                int riga, col;
                riga = r;
                col = c;

                // deriviamo una classe anonima
                this.campo[r][c].addMouseListener(new MouseAdapter() {
                    
                    // MouseListener interfaccia -> MouseAdapter classe astratta
                    // usiamo una classe astratta perché dobbiamo implementare troppi metodi astratti
                    // NON è possibile usare una lambda function! non saprebbe quale metodo stiamo richiamando

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        
                        Cella cliccata = (Cella)e.getSource();

                        // button1: pulsante sx
                        // button2: tasto centrale/rotellina
                        // button3: pulsante dx
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:
                                while (primoClick && campo[riga][col].getContenuto() == MINA){
                                    System.out.println("Rigenero");
                                    generaMine();
                                }
                                if (primoClick)
                                    contaIndizi();
                                
                                primoClick = false;
                                
                                // scopre la singola cella (o l'area vuota)
                                scopriCella(cliccata.getR(), cliccata.getC());


                                if(checkVittoria()){
                                    JOptionPane.showMessageDialog(null, "Hai vinto!");
                                    return;
                                }
                                break;

                            case MouseEvent.BUTTON3:
                                if (cliccata.isScoperta()) break;    

                                // se ho esaurito le bandierine
                                if (!cliccata.isBandiera() && nBandiere == mine) {
                                    JOptionPane.showMessageDialog(null, "Hai usato tutte le bandiere!");
                                    break;
                                }
                                
                                cliccata.toggleBandiera();

                                // se sono bandiera, incremento - se coperta, decremento
                                nBandiere += cliccata.isBandiera() ? +1 : -1;
                                break;
                        }

                        //super.mouseReleased(e);
                    }
                });
            }
        }

        // generare la posizione delle mine
        generaMine();

        // impostare gli indizi
        contaIndizi();
    }

    /**
     * Posiziona le mine casualmente nelle celle
     */
    private void generaMine() {
        for (int r = 0; r < campo.length; r++) {
            for (int c = 0; c < this.campo[0].length; c++) {
                campo[r][c].setContenuto(0);
            }
        }

        Random rand = new Random();

        for (int i = 0; i < this.mine; i++) {
            Cella newMina = campo[rand.nextInt(campo.length)][rand.nextInt(campo[0].length)];

            //Se è gia una mina decremento l'indice e vado avanti, altrimenti la imposto come mina
            if (newMina.getContenuto() == this.MINA)
                i--;
            else
                newMina.setContenuto(this.MINA);   
        }
    }

    /**
     * Analizza le celle adiacenti contando le mine, e imposta gli indizi numerici
     */
    private void contaIndizi() {
        //Ciclo su tutte le celle
        for (int r = 0; r < campo.length; r++) {
            for (int c = 0; c < campo[0].length; c++) {
                //Inizializzo il conto
                int conto = 0;
                //Prendo la cella
                Cella cella = campo[r][c];
                //Controllo se è una mina
                if (cella.getContenuto() == MINA) continue;
                //Controllo se sta ai bordi
                int[] bordi = controllaBordi(cella);

                for (int i = bordi[0]; i <= bordi[1]; i++) {
                    for (int j = bordi[2]; j <= bordi[3]; j++) {
                        if (campo[i][j].getContenuto() == -1)
                            conto++;
                    }
                }
                //Inserisco il valore nella cella
                cella.setContenuto(conto);
            }
        }
    }

    /**
     * Analizza e scopre le celle adiacenti se vuote
     * @param r riga della cella partenza
     * @param c colonna della cella di partenza
     */
    public void scopriCella(int r, int c) {
        //Salvo la cella in una variabile
        Cella cella = campo[r][c];

        //Controllo se è gia visibile o se è una bandiera e in caso non faccio niente
        if (cella.isBandiera() || cella.isScoperta()) 
            return;

        cella.setVisibile(true);

        //controllo se ho perso
        if(cella.getContenuto() == MINA){
            JOptionPane.showMessageDialog(null, "Game over!");
            System.exit(0);
        }

        int[] bordi = controllaBordi(cella);

        //Se la cella non ha mine adiacenti scopro tutte le sue celle adiacenti
        if (cella.getContenuto() == 0){
            /*//sopra
            scopriCella(bordi[0], bordi[2]);
            scopriCella(bordi[0], c);
            scopriCella(bordi[0], bordi[3]);
            //in mezzo
            scopriCella(r, bordi[2]);
            scopriCella(r, bordi[3]);
            //sotto
            scopriCella(bordi[1], bordi[2]);
            scopriCella(bordi[1], c);
            scopriCella(bordi[1], bordi[3]);
*/
        for(int riga = bordi[0]; riga <= bordi[1]; riga++){
            for(int col = bordi[2]; col <= bordi[3]; col++){
                scopriCella(riga, col);
            }
        }
        }
    }

    /**
     * Controlla se la cella è adiacente al bordo e ritorna un array con valori per il controllo delle celle adiacenti effettive.
     * Per esempio se una cella sta sul bordo sinistro, le celle adiacenti effettive sono solo sopra, a destra e sotto.
     * @param cella Cella da controllare
     * @return Array con i valori (rMin, rMax, cMin, cMax)
     */
    private int[] controllaBordi(Cella cella){
        // int rMin, rMax, cMin, cMax;
        int[] bordi = {0,0,0,0};

        //rMin
        if (cella.getR() -1 < 0) bordi[0] = cella.getR();
        else bordi[0] = cella.getR() -1;
        //rMax
        if (cella.getR() +1 >= campo.length) bordi[1] = cella.getR();
        else bordi[1] = cella.getR() +1;
        //cMin
        if (cella.getC() -1 < 0) bordi[2] = cella.getC();
        else bordi[2] = cella.getC() -1;
        //cMax
        if (cella.getC() +1 >= campo[0].length) bordi[3] = cella.getC();
        else bordi[3] = cella.getC() +1;

        return bordi;
    }

    private boolean checkVittoria(){
        boolean win = true;
        //Label, identifica il ciclo più esterno per poter fare il break su di esso
        ciclo:
        for (int r = 0; r < campo.length; r++) {
            for (int c = 0; c < campo[0].length; c++) {
                if(campo[r][c].getContenuto() != this.MINA && !campo[r][c].isScoperta()){
                    win = false;
                    break ciclo;
                }
            }
        }
        return win;
    }
}
