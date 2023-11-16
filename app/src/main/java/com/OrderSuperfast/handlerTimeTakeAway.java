package com.OrderSuperfast;

import android.os.Handler;

import com.OrderSuperfast.Modelo.Clases.TakeAwayPedido;

public class handlerTimeTakeAway {
    private TakeAwayPedido takeAway;
    private Handler handler;

    public handlerTimeTakeAway(TakeAwayPedido pPedido,Handler pHandler){
        this.takeAway=pPedido;
        this.handler=pHandler;
    }

    public TakeAwayPedido getTakeAway(){
        return this.takeAway;
    }

    public void resetHandler(long time){

        handler.removeCallbacksAndMessages(null);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },time);
    }
}
