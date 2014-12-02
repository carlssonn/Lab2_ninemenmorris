package com.lab2.graphical;

import com.example.lab2_ninemenmorris.R;
import com.example.lab2_ninemenmorris.Unitinfo;

import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*
 	Width = 454
	To first box: 78           78/454=0,1718=17,18%
	To second box: 148    148/454=0,3259=32,6%

	Height = 451
	To first box: 71			71/451=0,1574=15,74%
	To second box: 142		142/451=0,3148=31,48%
 */

public class Gameboard extends ActionBarActivity{

	private ImageDraw drawPiece;
    private Rect wholeGameboard;
    private int widthSecRect=0;
    private int heightSecRect=0;
    private ImageView gameBoard;
    private int[] xCords=new int[25];
    private int[] yCords=new int[25];
    private boolean initDone=false;
    private Unitinfo unitInfo;
    private int checkIfValidPos=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameboard);
        
        gameBoard = (ImageView) findViewById(R.id.imgGameboard);
        
        
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.relaLayout);
        
        
        drawPiece = new ImageDraw(getApplicationContext());
        layout.addView(drawPiece);
        
        //Rect rect = new Rect(gameBoard.getLeft(),gameBoard.getTop(), gameBoard.getRight(), gameBoard.getBottom());
        
        layout.setOnTouchListener(new OnTouchListener() {
           
            public boolean onTouch(View v, MotionEvent ev) {
                switch(ev.getAction()){
                case MotionEvent.ACTION_UP:
                	if(initDone==false){
                		initializeGameBoard();
                		unitInfo = new Unitinfo(xCords, yCords);
                		initDone=true;
                	}
                	checkIfValidPos=unitInfo.checkIfInBound(ev.getX(), ev.getY());
                	if(checkIfValidPos!=-1){
                		drawPiece.drawCircle(xCords[checkIfValidPos], yCords[checkIfValidPos]);	
                		checkIfValidPos=-1;
                	}else{
                		Toast.makeText(getApplicationContext(), "You wong", Toast.LENGTH_SHORT);
                	}
                	
                	
                	//System.out.println("X: "+ev.getX()+" Y: "+ev.getY());
                	//drawPiece.drawCircle(ev.getX(), ev.getY());
                	
                	//System.out.println("TopLeft corner: X"+gameBoard.getLeft()+" Y: "+gameBoard.getTop());
                	
                	
            		
                	
                	/*
                	int[] pos = new int[2];
                	
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    
                    
                    float x1 = ev.getX()*(160f/dm.densityDpi);
                    float y1 = ev.getY()*(160f/dm.densityDpi);
                   
                    
                    System.out.println("TopLeft corner: X"+gameBoard.getLeft()+" Y: "+gameBoard.getTop());
                    System.out.println("TopRight corner: X"+gameBoard.getRight()+" Y: "+gameBoard.getTop());
                    System.out.println("BottomRight corner: X"+gameBoard.getLeft()+" Y: "+gameBoard.getBottom());
                    System.out.println("BottomLeft corner: X"+gameBoard.getRight()+" Y: "+gameBoard.getBottom()+"\n\n\n");
                    
                    System.out.println("TopLeftSecRect corner: X"+gameBoard.getLeft()*3+" Y: "+gameBoard.getTop());
                    
                    System.out.println("the X CORD: "+(ev.getX()-gameBoard.getLeft())+"the Y CORD: "+(ev.getY()-gameBoard.getTop()));
                    //System.out.println("LARGE RECT: Get left(): "+gameBoard.getLeft()+" getTop(): "+gameBoard.getTop()+" getRight(): "+gameBoard.getRight()+" getBottom(): "+gameBoard.getBottom());
                    //System.out.println("SEC RECT: Get left(): "+(int)(gameBoard.getLeft()*1.17)+" getTop(): "+(int)(gameBoard.getTop()*1.55)+" getRight(): "+(int)(gameBoard.getRight()*0.775)+" getBottom(): "+(int)(gameBoard.getBottom()*0.4542));
                   
                    wholeGameboard=new Rect((int)(gameBoard.getLeft()*2.2),(int)(gameBoard.getTop()*2.2),(int)(gameBoard.getRight()*0.775), (int)(gameBoard.getBottom()*0.4542));
                   
                    //System.out.println("Touch event x: "+x1+ " y: "+y1);
                    System.out.println("X CORDS: "+ev.getX()+"Y CORDS: "+ev.getY());
                    //wholeGameboard=new Rect(gameBoard.getLeft(),gameBoard.getTop(), gameBoard.getRight(), gameBoard.getBottom());
                   
                   
                   
                    if(wholeGameboard.contains((int)ev.getX(), (int)ev.getY())){
                            System.out.println("X CORDS: "+ev.getX()+"Y CORDS: "+ev.getY());
                    }*/
                   
                    break;
               
                }
                    return true;
            }
        });
        
    }
    public void initializeGameBoard(){
	      //Rad0
        xCords[0]=0+gameBoard.getLeft();//gameBoard.getLeft()-gameBoard.getLeft();
        yCords[0]=0+gameBoard.getTop();//gameBoard.getTop()-gameBoard.getTop();
        xCords[1]=(gameBoard.getWidth()/2)+gameBoard.getLeft();
        yCords[1]=0+gameBoard.getTop();
        xCords[2]=gameBoard.getWidth()+gameBoard.getLeft();
        yCords[2]=0+gameBoard.getTop();
        //Rad1
        xCords[3]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[3]=((gameBoard.getHeight()/6)*1)+gameBoard.getTop();
        xCords[4]=(gameBoard.getWidth()/2)+gameBoard.getLeft();
        yCords[4]=(gameBoard.getHeight()/2/3)+gameBoard.getTop();
        xCords[5]=((gameBoard.getWidth()/2/3)*5)+gameBoard.getLeft();
        yCords[5]=(gameBoard.getHeight()/2/3)+gameBoard.getTop();
        //Rad2
        xCords[6]=((gameBoard.getWidth()/2/3)*2)+gameBoard.getLeft();
        yCords[6]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        xCords[7]=((gameBoard.getWidth()/2/3)*3)+gameBoard.getLeft();
        yCords[7]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        xCords[8]=((gameBoard.getWidth()/2/3)*4)+gameBoard.getLeft();
        yCords[8]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        //Rad3
        xCords[9]=0+gameBoard.getLeft();
        yCords[9]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[10]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[10]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[11]=((gameBoard.getWidth()/6)*2)+gameBoard.getLeft();
        yCords[11]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[12]=((gameBoard.getWidth()/6)*4)+gameBoard.getLeft();
        yCords[12]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[13]=((gameBoard.getWidth()/6)*5)+gameBoard.getLeft();
        yCords[13]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[14]=((gameBoard.getWidth()/6)*6)+gameBoard.getLeft();
        yCords[14]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        //Rad4
        xCords[15]=((gameBoard.getWidth()/6)*2)+gameBoard.getLeft();
        yCords[15]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        xCords[16]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[16]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        xCords[17]=((gameBoard.getWidth()/6)*4)+gameBoard.getLeft();
        yCords[17]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        //Rad5
        xCords[18]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[18]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        xCords[19]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[19]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        xCords[20]=((gameBoard.getWidth()/6)*5)+gameBoard.getLeft();
        yCords[20]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        //Rad6
        xCords[21]=((gameBoard.getWidth()/6)*0)+gameBoard.getLeft();
        yCords[21]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
        xCords[22]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[22]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
        xCords[23]=((gameBoard.getWidth()/6)*6)+gameBoard.getLeft();
        yCords[23]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
         
  }
	
}