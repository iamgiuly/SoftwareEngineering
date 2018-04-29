package com.ids.ids.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.ids.ids.entity.Nodo;

public class NodoView {

    private Context context;

    private Nodo nodo;
    private int lunghezzaMappa;
    private int altezzaMappa;

    private Bitmap image;

    public NodoView(Nodo nodo, int lunghezzaMappa, int altezzaMappa, Context context) {
        this.nodo = nodo;
        this.context = context;

        this.lunghezzaMappa = lunghezzaMappa;
        this.altezzaMappa = altezzaMappa;

        int image = nodo.getImage();
        this.setImage(image);
    }

    public Nodo getNodo() {

        return nodo;
    }

    public void setNodo(Nodo nodo) {

        this.nodo = nodo;
    }

    public int getId() {

        return nodo.getId();
    }

    public int getX() {

        return xAssoluta(nodo.getX());
    }

    public int getXStart() {

        return this.getX() - Nodo.DIM / 2;
    }

    public int getXEnd() {

        return this.getX() + Nodo.DIM / 2;
    }

    public int getY() {

        return yAssoluta(nodo.getY());
    }

    public int getYStart() {

        return this.getY() - Nodo.DIM / 2;
    }

    public int getYEnd() {

        return this.getY() + Nodo.DIM / 2;
    }

    public Bitmap getImage() {

        return image;
    }

    public void setImage(int image) {

        this.image = BitmapFactory.decodeResource(context.getResources(), image);
    }

    public Rect getRect() {

        return new Rect(this.getXStart(), this.getYStart(), this.getXEnd(), this.getYEnd());
    }

    /**
     * Calcola la coordinata X assoluta di un nodo
     *
     * @param xNodoRelativa valore da 0 a 100 con riferimento alla posizione relativa alla lunghezza della mappa
     * @return coordinata X espressa in pixel sullo schermo
     */
    private int xAssoluta(int xNodoRelativa) {

        return (this.lunghezzaMappa * xNodoRelativa) / 100;
    }

    /**
     * Calcola la coordinata Y assoluta di un nodo
     *
     * @param yNodoRelativa valore da 0 a 100 con riferimento alla posizione relativa all'altezza della mappa
     * @return coordinata Y espressa in pixel sullo schermo
     */
    private int yAssoluta(int yNodoRelativa) {

        return (this.altezzaMappa * yNodoRelativa) / 100;
    }
}
