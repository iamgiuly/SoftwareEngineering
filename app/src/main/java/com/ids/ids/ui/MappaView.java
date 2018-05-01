package com.ids.ids.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MappaView extends View {

    private int width;
    private int height;
    private Context context;
    private Paint paint;
    private Paint paintArcoNormale;
    private Paint paintArcoPercorso;

    private Bitmap image;
    private Mappa mappa;

    private ArrayList<NodoView> nodi;
    private Nodo posUtente;
    private ArrayList<Arco> percorso;

    private boolean disegnaPercorso = false;
    private boolean rendered = false;

    public MappaView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        this.nodi = new ArrayList<>();
        this.percorso = new ArrayList<>();

        this.paint = new Paint();
        this.initPaintArcoNormale();
        this.initPaintArcoPercorso();
    }

    private void initPaintArcoNormale() {
        paintArcoNormale = new Paint();
        paintArcoNormale.setAntiAlias(true);
        paintArcoNormale.setColor(Color.BLUE);
        paintArcoNormale.setStyle(Paint.Style.STROKE);
        paintArcoNormale.setStrokeJoin(Paint.Join.ROUND);
        paintArcoNormale.setStrokeWidth(5f);
    }

    private void initPaintArcoPercorso() {
        paintArcoPercorso = new Paint();
        paintArcoPercorso.setAntiAlias(true);
        paintArcoPercorso.setColor(Color.GREEN);
        paintArcoPercorso.setStyle(Paint.Style.STROKE);
        paintArcoPercorso.setStrokeJoin(Paint.Join.ROUND);
        paintArcoPercorso.setStrokeWidth(8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.image != null) {
            canvas.drawBitmap(this.image, null, new Rect(0, 0, this.width, this.height), this.paint);
           for (NodoView nodoView : this.nodi) {
                Bitmap image = nodoView.getNodo().equals(this.posUtente)
                        ? BitmapFactory.decodeResource(context.getResources(), Nodo.IMG_UTENTE)
                        : nodoView.getImage();
                canvas.drawBitmap(image, null, nodoView.getRect(), this.paint);
            }
            if (this.disegnaPercorso)
                this.disegnaPercorso(canvas);
        }
    }

    private void disegnaPercorso(Canvas canvas) {
        ArrayList<Arco> archi = this.mappa.getArchi();
        if (archi == null) return;
        for (Arco arco : archi)
            this.disegnaArcoTraNodi(canvas,
                    this.getNodoViewFromNodo(arco.getNodoPartenza()),
                    this.getNodoViewFromNodo(arco.getNodoArrivo()),
                    this.percorso.contains(arco));
    }

    private void disegnaArcoTraNodi(Canvas canvas, NodoView nodo1, NodoView nodo2, boolean percorso) {
        Paint p = percorso ? this.paintArcoPercorso : this.paintArcoNormale;
        canvas.drawLine(nodo1.getX(), nodo1.getY(), nodo2.getX(), nodo2.getY(), p);
    }

    private NodoView getNodoViewFromNodo(Nodo nodo) {
        for (NodoView nodoView : this.nodi)
            if (nodoView.getId() == nodo.getId())
                return nodoView;
        return null;
    }

    public void setMappa(Mappa map) throws FileNotFoundException {

        this.setMappa(map, false);
    }

    public void setPosUtente(Nodo posUtente) {

        this.posUtente = posUtente;
    }

    public void setMappa(Mappa map, boolean disegnaPercorso) throws FileNotFoundException {
        this.mappa = map;
        this.disegnaPercorso = disegnaPercorso;
        this.rendered = false;
        this.nodi.clear();
        this.percorso.clear();

        image = BitmapFactory.decodeStream(context.openFileInput(mappa.getPiantina() + ".png"));

        ViewTreeObserver viewTree = this.getViewTreeObserver();
        viewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (!rendered) {
                    width = getMeasuredWidth();
                    height = getMeasuredHeight();
                    for (Nodo nodo : mappa.getNodi()) {
                        NodoView nodoView = new NodoView(nodo, width, height, context);
                        nodi.add(nodoView);
                    }
                    rendered = true;
                }
                return true;
            }
        });
    }

    public void setNodi(ArrayList<Nodo> nodiAggiornati){

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        for (Nodo nodo : nodiAggiornati) {
            NodoView nodoView = new NodoView(nodo, width, height, context);
            nodi.add(nodoView);
        }
    }

    public void setPercorso(ArrayList<Arco> percorso) {

        this.percorso = percorso;
    }

    public void messaggio(String titolo,String messaggio,boolean enable){

        AlertDialog avviso = new AlertDialog.Builder(context).create();
        avviso.setTitle(titolo);
        avviso.setMessage(messaggio);
        avviso.setCanceledOnTouchOutside(enable);
        avviso.show();

    }

    public NodoView getNodoPremuto(int x, int y) {
        for (NodoView nodo : this.nodi)
            if (nodo.getRect().contains(x, y))
                return nodo;
        return null;
    }

    public void deleteImagePiantina(){

        context.deleteFile(mappa.getPiantina() + ".png");
    }
}
