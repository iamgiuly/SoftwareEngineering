package com.ids.ids.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;

import java.util.ArrayList;

public class MappaView extends View {

    private int width;
    private int height;
    private Context context;
    private Paint mPaint;

    private Bitmap image;
    private Mappa mappa;

    private ArrayList<NodoView> nodi;

    private boolean disegnaPercorso = false;
    private boolean rendered = false;

    public MappaView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        this.nodi = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.image, null, new Rect(0, 0, this.width, this.height), this.mPaint);
        for(NodoView nodo : this.nodi)
            canvas.drawBitmap(nodo.getImage(), null, nodo.getRect(), this.mPaint);
        if(this.disegnaPercorso)
            this.disegnaPercorso(canvas);
    }

    private void disegnaPercorso(Canvas canvas){
        ArrayList<Arco> archi = this.mappa.getArchi();
        if(archi == null) return;
        for(Arco arco : archi)
            this.disegnaArcoTraNodi(canvas,
                                    this.getNodoViewFromNodo(arco.getNodoPartenza()),
                                    this.getNodoViewFromNodo(arco.getNodoArrivo()));
        //TODO contrassegna archi percorso
    }

    private void disegnaArcoTraNodi(Canvas canvas, NodoView nodo1, NodoView nodo2){
        canvas.drawLine(nodo1.getX(), nodo1.getY(), nodo2.getX(), nodo2.getY(), this.mPaint);
    }

    private NodoView getNodoViewFromNodo(Nodo nodo){
        for(NodoView nodoView : this.nodi)
            if(nodoView.getId() == nodo.getId())
                return nodoView;
        return null;
    }

    public void setMappa(Mappa map){
        this.mappa = map;
        this.disegnaPercorso = false;
        this.rendered = false;
        this.nodi.clear();

        //TODO FIXARE OUTOFMEMORY ERROR (È INVIA NODI CHE FA CRASHARE, TORNARE INDIETRO NO)
        image = BitmapFactory.decodeResource(getResources(), mappa.getPiantina());

        ViewTreeObserver viewTree = this.getViewTreeObserver();
        viewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if(!rendered) {
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

    public NodoView getNodoPremuto(int x, int y){
        for(NodoView nodo : this.nodi)
            if(nodo.getRect().contains(x, y))
                return nodo;
        return null;
    }

    public void setDisegnaPercorso(boolean disegnaPercorso){
        this.disegnaPercorso = disegnaPercorso;
    }

}
