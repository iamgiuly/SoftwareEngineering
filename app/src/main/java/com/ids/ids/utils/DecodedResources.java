package com.ids.ids.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ids.ids.entity.Mappa;
import com.ids.ids.ui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * In questa classe vengono salvate le immagini Bitmap come variabili statiche,
 * in questo modo eviteremo di ricrearle ogni volta occupando troppa memoria
 */
public class DecodedResources {

    private static DecodedResources instance = null;

    // la chiave sarà l'id della mappa (non la mappa stessa, perché si potrebbe non far riferimento sempre allo stesso oggetto),
    // il valore sarà l'immagine Bitmap associata
    public Map<Integer, Bitmap> preloadedImages = new HashMap<>();

    public Bitmap getPreloadedImage(Context context, Mappa mappa){
        int id = mappa.getPiano();
        if(preloadedImages.containsKey(id))
            return preloadedImages.get(id);
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), DebugSettings.PIANO_DRAWABLE_DEFAULT); //TODO mappa.getPiantina());
        preloadedImages.put(id, image);
        return image;
    }

    public static DecodedResources getInstance(){
        if(instance == null)
            instance = new DecodedResources();
        return instance;
    }
}
