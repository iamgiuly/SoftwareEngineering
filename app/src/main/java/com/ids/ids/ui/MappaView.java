package com.ids.ids.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;

import java.util.ArrayList;

public class MappaView extends View {

    private int width;
    private int height;
    private Context context;
    private Paint mPaint;
    private boolean rendered = false;

    private Bitmap image;
    private Mappa mappa;

    private ArrayList<NodoView> nodi;


    private Button button1, button2;        //TODO

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

    private void visualizzaMappa(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawBitmap(this.image, null, new RectF(0, 0, this.width, this.height), this.mPaint);
        canvas.drawBitmap(this.image, null, new Rect(0, 0, this.width, this.height), this.mPaint);
        canvas.drawLine(10, 10, 100, 100, this.mPaint);     //TODO
        for(NodoView nodo : this.nodi)
            canvas.drawBitmap(nodo.getImage(), null, nodo.getRect(), this.mPaint);
        //TODO disegna percorso
    }

    public void disegnaPercorso(){

    }

    public void setMappa(Mappa map){
        this.mappa = map;
        //TODO FIXARE OutOfMemoryError
        image = BitmapFactory.decodeResource(getResources(), mappa.getPiantina());
        this.nodi.clear();

        ViewTreeObserver viewTree = this.getViewTreeObserver();
        viewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if(!rendered){
                    width = getMeasuredWidth();
                    height = getMeasuredHeight();
                    for(Nodo nodo : mappa.getNodi()){
                        NodoView nodoView = new NodoView(nodo, width, height, context);
                        nodi.add(nodoView);
                    }
                    visualizzaMappa();
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

}
