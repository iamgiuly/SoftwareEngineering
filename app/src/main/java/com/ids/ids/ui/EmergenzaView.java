package com.ids.ids.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.DebugSettings;
import com.ids.ids.utils.DecodedResources;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EmergenzaView extends View {

    private Context context;

    public EmergenzaView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
