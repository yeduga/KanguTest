package com.virtualtec.kangutest.ClassesCustom;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Daniel Pardo Ligorred @ www.yizztech.com Based ON (Sergei)
 *         http://weavora.com/blog/2012/02/07/android-how-to-use-animated-gif/
 */
public class GifView extends android.support.v7.widget.AppCompatImageView {

    private Context context;
    private Double scaleX, scaleY;
    private Movie movie;
    private long moviestart;

    public GifView(Context context) {

        super(context);
        this.context = context;
    }

    public GifView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.context = context;
    }

    public void loadGIFResource(int id) {

        InputStream is = this.context.getResources().openRawResource(id);

        this.movie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        super.onDraw(canvas);

        long currentTime = android.os.SystemClock.uptimeMillis();

        if (this.moviestart == 0) {

            this.scaleX = (double) this.getWidth()
                    / (double) this.movie.width();
            this.scaleY = (double) this.getHeight()
                    / (double) this.movie.height();

            this.moviestart = currentTime;
        }

        if (movie != null) {

            this.movie
                    .setTime((int) ((currentTime - this.moviestart) % this.movie
                            .duration()));

            canvas.scale(this.scaleX.floatValue(), this.scaleY.floatValue());
            this.movie.draw(canvas, this.scaleX.floatValue(),
                    this.scaleY.floatValue());
        }

        this.invalidate();
    }
}