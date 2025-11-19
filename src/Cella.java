import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Cella extends Canvas {

    private int contenuto;
    private final int MINA = -1;
    private StatoCella stato;
    private int r,c;

    // colori dei numeri
    private Color[] colori;

    public Cella(int dim, int r, int c) {

        colori = new Color[] {
            Color.white,
            Color.blue,
            new Color(70, 170, 70),   // green
            Color.red,
            new Color(0, 0, 128),     // dark blue
            new Color(150, 50, 50),   // brown
            new Color(50, 255, 200),  // aqua
            Color.darkGray,
            Color.black
        };

        setSize(dim, dim);
        this.r = r;
        this.c = c;
        this.contenuto = 0;
        this.stato = StatoCella.Coperta;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setFont(new Font("Consolas", Font.BOLD, 16));

        // estendiamo il metodo
        switch (stato) {
            case Coperta:
                g.setColor(Color.orange);
                g.fillRect(0, 0, getWidth(), getHeight());
                break;
        
            case Scoperta:
                g.setColor(Color.lightGray);
                g.fillRect(0, 0, getWidth(), getHeight());


                //Se c'è una mina
                if(contenuto == MINA){
                    g.setColor(Color.red);
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else if (contenuto > 0){
                        g.setColor(colori[contenuto]);
                        g.drawString(contenuto + "", getWidth()/3, 2*getHeight()/3);
                }
                break;

            case Bandiera:
                //Cella coperta
                g.setColor(Color.orange);
                g.fillRect(0, 0, getWidth(), getHeight());

                //Bandierina
                g.setColor(Color.black);
                g.fillRect(5, 5, getWidth()/7, getHeight() -5);
                g.setColor(Color.red);

                //Disegno un triangolo come poligono custom
                int[] xPoints = {getWidth()/3, getWidth() -5, getWidth()/3};
                int[] yPoints = {5, 2*getHeight()/5, 2*getHeight()/3};
                g.fillPolygon(xPoints, yPoints, 3);
                break;
        }

        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth()-1, getHeight() -1);
    }

    public int getContenuto() {
        return contenuto;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }

    public boolean isScoperta() {
        return stato == StatoCella.Scoperta;
    }

    public boolean isBandiera() {
        return stato == StatoCella.Bandiera;
    }

    public void setContenuto(int contenuto) {
        if (contenuto >= -1 && contenuto <= 8)
            this.contenuto = contenuto;
    }

    // scambia tra bandiera e coperta
    public void toggleBandiera() {
        if (stato == StatoCella.Coperta)
            stato = StatoCella.Bandiera;
        else
            stato = StatoCella.Coperta;

        repaint();
    }

    public void setVisibile(boolean mostra) {

        if (mostra && stato == StatoCella.Coperta)
            stato = StatoCella.Scoperta;

        repaint();
    }
}
