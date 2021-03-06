package com.lab2.graphical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.conn.scheme.PlainSocketFactory;

import com.example.lab2_ninemenmorris.GameboardInfo;
import com.example.lab2_ninemenmorris.Gameplay;
import com.example.lab2_ninemenmorris.MainActivity;
import com.example.lab2_ninemenmorris.NineMenMorrisRules;
import com.example.lab2_ninemenmorris.R;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Gameboard extends ActionBarActivity{

	private static final String filename = "savedGameSession";
	private static final String PREFERENCES_NAME="SaveFile";
	private static final String IS_SAVED="FileSaved";
	private ImageDraw drawPiece;
    private ImageView gameBoard;
    private TextView tvPlayersTurn;
    private int[] xCords=new int[25];
    private int[] yCords=new int[25];
    private boolean initDone=false;
    private Gameplay gamePlay;
    private int checkIfValidPos=-1;
    private GameboardInfo gbInfo=null;
    private ArrayList<Piece> pieces = null;
    private FileWriter fileWriter= null;
    private SharedPreferences myPreferences;
    private NineMenMorrisRules nmm = null;
    private boolean restartGame=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameboard);
        gameBoard = null;
        gameBoard = (ImageView) findViewById(R.id.imgGameboard);
        tvPlayersTurn = (TextView) findViewById(R.id.txtViewPTurn);
        
        
        myPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        
        //gets a boolean value if the game is saved or not
        boolean isSaved = myPreferences.getBoolean(IS_SAVED, false);
        
        
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.relaLayout);
        
        if(myPreferences.getBoolean(IS_SAVED, false)){
        	gbInfo = new GameboardInfo();
        	InputStream inputStream=null;
        	InputStreamReader streamReader=null;
        	BufferedReader bufferedReader=null;
        	StringBuffer storedString = new StringBuffer();
        	String[] arrayFromFile=null;
        	int[] piecePos=new int[25];
        	int playerTurn = -1;
        	
        	try {
				inputStream=openFileInput(filename);
				streamReader = new InputStreamReader(inputStream);
	            String strLine = null;
	            bufferedReader=new BufferedReader(streamReader);
	            if ((strLine = bufferedReader.readLine()) != null) {
	                storedString.append(strLine);
	                arrayFromFile=strLine.split(",");
	                for(int i=1;i<25;i++){
	                	piecePos[converterFromPosToNMM(i)]=Integer.parseInt(arrayFromFile[i-1]);
	                }
	                playerTurn=Integer.parseInt(arrayFromFile[24]);
	                
	                gbInfo.setPiecesPos(piecePos);
	                gbInfo.setPlayerTurn(playerTurn);
	                gbInfo.savePieces(Integer.parseInt(arrayFromFile[25]), Integer.parseInt(arrayFromFile[26]));
	                nmm=gbInfo.initNmm();
	                
	                drawPiece = new ImageDraw(getApplicationContext());
	                layout.addView(drawPiece);
	                
	                ViewTreeObserver vto = gameBoard.getViewTreeObserver();
	                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
	                    public boolean onPreDraw() {
	                    	gameBoard.getViewTreeObserver().removeOnPreDrawListener(this);
	                    	
	                    	initializeGameBoard();
	                    	pieces=null;
	                		pieces = new ArrayList<Piece>();
	                		for(int i=1;i<gbInfo.getPiecesPos().length;i++){
	                			if(gbInfo.getPiecesPos()[i]==4){
	                				Paint paint = new Paint();
	                        		paint.setColor(Color.BLUE);
	                				pieces.add(new Piece(xCords[i], yCords[i], paint));
	                			
	                			}else if(gbInfo.getPiecesPos()[i]==5){
	                				Paint paint = new Paint();
	                        		paint.setColor(Color.RED);
	                				pieces.add(new Piece(xCords[i], yCords[i], paint));
	                			}
	                		}
	                		
	                		
	                		
	                		drawPiece.drawCircle(pieces);
	                        return true;
	                    }
	                });
	                //Redraw pieces from last saved session
	                
	                
	                if(gbInfo.getPlayerTurn() == 1){
            			tvPlayersTurn.setText("BLUE PLAYER TURN");
            		}else{
            			tvPlayersTurn.setText("RED PLAYER TURN");
            		}

	            }
			}catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "Can not read from file, try again later or start a new game", Toast.LENGTH_SHORT).show();
			}
            
        }else{
        	tvPlayersTurn.setText("RED PLAYER TURN");
        	drawPiece = new ImageDraw(getApplicationContext());
            layout.addView(drawPiece);
        }
        
        

        layout.setOnTouchListener(new OnTouchListener() {
           
            public boolean onTouch(View v, MotionEvent ev) {
            	//Init important classes
            	if(initDone==false){
            		initializeGameBoard();
            		if(!myPreferences.getBoolean(IS_SAVED, false)){
            			gamePlay = new Gameplay(xCords, yCords);
            		}else{
            			gamePlay = new Gameplay(xCords, yCords, nmm, gbInfo);
            		}
            		
            		initDone=true;
            	}
                switch(ev.getAction()){
                case MotionEvent.ACTION_UP:
                	
                	System.out.println("Check pos: X: "+ev.getX()+" Y: "+ev.getY());
                	
                	checkIfValidPos=gamePlay.checkIfInBound(ev.getX(), ev.getY());
                	
                	if(checkIfValidPos!=-1){
                		if(!myPreferences.getBoolean(IS_SAVED, false)){
                			gbInfo = gamePlay.getGbInfo();
                		}
                		
                		initializeGameBoard();
                		System.out.println("xCords: "+xCords[1]);
                		//Draw pieces on board
    	                pieces=null;
                		pieces = new ArrayList<Piece>();
                		for(int i=1;i<gbInfo.getPiecesPos().length;i++){
                			if(gbInfo.getPiecesPos()[i]==4){
                				
                				Paint paint = new Paint();
                        		paint.setColor(Color.BLUE);
                				pieces.add(new Piece(xCords[i], yCords[i], paint));
                			
                			}else if(gbInfo.getPiecesPos()[i]==5){
                				Paint paint = new Paint();
                        		paint.setColor(Color.RED);
                				pieces.add(new Piece(xCords[i], yCords[i], paint));
                			}
                		}
                		
                		
                		
                		drawPiece.drawCircle(pieces);
                		
                		//gbInfo.getPiecesPos();
                		
                		//If game won by one player show alertdialog
                		if(gbInfo.getMessageInfo().contains("Congratulations")){
                			Toast.makeText(getApplicationContext(), gbInfo.getMessageInfo(), Toast.LENGTH_SHORT).show();
                			new AlertDialog.Builder(Gameboard.this)
        		            .setTitle("Congratulations")
        		            .setMessage(gbInfo.getMessageInfo()+" Do you want to restart or cancel?")
        		            .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
        		                public void onClick(DialogInterface dialog, int which) { 
        		                	Intent i = new Intent(getApplicationContext(),Gameboard.class);
        		                	SharedPreferences.Editor editor=myPreferences.edit();
        		            		editor.putBoolean(IS_SAVED, false);
        		            		editor.commit();
        		                	finish();
        		                }
        		             })
        		            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		                public void onClick(DialogInterface dialog, int which) {
        		                	SharedPreferences.Editor editor=myPreferences.edit();
        		            		editor.putBoolean(IS_SAVED, false);
        		            		editor.commit();
        		                	finish();
        		                }
        		             }).show();
                			
                		}else if(!gbInfo.getMessageInfo().equalsIgnoreCase("-1")){ //Shows different moves and wrongs
                			Toast.makeText(getApplicationContext(), gbInfo.getMessageInfo(), Toast.LENGTH_SHORT).show();
                		}
                		//Shows whos turn
                		if(gbInfo.getPlayerTurn() == 1){
                			tvPlayersTurn.setText("BLUE PLAYER TURN");
                		}else{
                			tvPlayersTurn.setText("RED PLAYER TURN");
                		}
                		
                		//Resets onTouch of hitboxes
                		checkIfValidPos=-1;
                	}else{
                		Toast.makeText(getApplicationContext(), "Not accurate enough...", Toast.LENGTH_SHORT).show();
                		
                	}
                   
                    break;
                case MotionEvent.ACTION_DOWN:
                	
                	break;
                }
                
                    return true;
            }
			
        });
        
    }
    
    
    @Override
    public void onPause(){
    	super.onPause();
    	if(initDone){
    		if(!restartGame){
        		save();
            	SharedPreferences.Editor editor=myPreferences.edit();
        		editor.putBoolean(IS_SAVED, true);
        		editor.commit();
        	}else{
        		restartGame=false;
        	}
    	}
    	
    	finish();
    }
    
    //Saves all importat data from current session to file
    private void save(){
    	String data="";
    	
    	for(int i=1;i<25;i++){
    		data+=gbInfo.getPiecesPos()[i]+",";
    	}
    	data+=gbInfo.getPlayerTurn()+",";
    	data+=gbInfo.getBluemarker()+",";
    	data+=gbInfo.getRedmarker();
    	
        try {
        	fileWriter = new FileWriter(new File(getApplication().getFilesDir(),filename));
        	fileWriter.write(data);
            fileWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //Convert our array pos to NMMRules class array
    public int converterFromPosToNMM(int pos){
    	switch (pos){
    	
    	case 1:
    		return 3;
    	case 2:
			return 6;
		case 3:
			return 9;
		case 4:
			return 2;
		case 5:
			return 5;
		case 6:
			return 8;
		case 7:
			return 1;
		case 8:
			return 4;
		case 9:
			return 7;
		case 10:
			return 24;
		case 11:
    		return 23;
		case 12:
			return 22;
		case 13:
    		return 10;
		case 14:
			return 11;
		case 15:
    		return 12;
		case 16:
    		return 19;
		case 17:
    		return 16;
		case 18:
			return 13;
		case 19:
    		return 20;
		case 20:
    		return 17;
		case 21:
    		return 14;
		case 22:
    		return 21;
		case 23:
    		return 18;
		case 24:
    		return 15;
    		default:
    			return -1;
    	}
    	
    }
    
    //Init coordinates from screen dpi to array
    public void initializeGameBoard(){
	      //Rad0
        xCords[1]=0+gameBoard.getLeft();//gameBoard.getLeft()-gameBoard.getLeft();
        yCords[1]=0+gameBoard.getTop();//gameBoard.getTop()-gameBoard.getTop();
        xCords[2]=(gameBoard.getWidth()/2)+gameBoard.getLeft();
        yCords[2]=0+gameBoard.getTop();
        xCords[3]=gameBoard.getWidth()+gameBoard.getLeft();
        yCords[3]=0+gameBoard.getTop();
        //Rad1
        xCords[4]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[4]=((gameBoard.getHeight()/6)*1)+gameBoard.getTop();
        xCords[5]=(gameBoard.getWidth()/2)+gameBoard.getLeft();
        yCords[5]=(gameBoard.getHeight()/2/3)+gameBoard.getTop();
        xCords[6]=((gameBoard.getWidth()/2/3)*5)+gameBoard.getLeft();
        yCords[6]=(gameBoard.getHeight()/2/3)+gameBoard.getTop();
        //Rad2
        xCords[7]=((gameBoard.getWidth()/2/3)*2)+gameBoard.getLeft();
        yCords[7]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        xCords[8]=((gameBoard.getWidth()/2/3)*3)+gameBoard.getLeft();
        yCords[8]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        xCords[9]=((gameBoard.getWidth()/2/3)*4)+gameBoard.getLeft();
        yCords[9]=((gameBoard.getHeight()/2/3)*2)+gameBoard.getTop();
        //Rad3
        xCords[10]=0+gameBoard.getLeft();
        yCords[10]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[11]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[11]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[12]=((gameBoard.getWidth()/6)*2)+gameBoard.getLeft();
        yCords[12]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[13]=((gameBoard.getWidth()/6)*4)+gameBoard.getLeft();
        yCords[13]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[14]=((gameBoard.getWidth()/6)*5)+gameBoard.getLeft();
        yCords[14]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        xCords[15]=((gameBoard.getWidth()/6)*6)+gameBoard.getLeft();
        yCords[15]=((gameBoard.getHeight()/6)*3)+gameBoard.getTop();
        //Rad4
        xCords[16]=((gameBoard.getWidth()/6)*2)+gameBoard.getLeft();
        yCords[16]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        xCords[17]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[17]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        xCords[18]=((gameBoard.getWidth()/6)*4)+gameBoard.getLeft();
        yCords[18]=((gameBoard.getHeight()/6)*4)+gameBoard.getTop();
        //Rad5
        xCords[19]=((gameBoard.getWidth()/6)*1)+gameBoard.getLeft();
        yCords[19]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        xCords[20]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[20]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        xCords[21]=((gameBoard.getWidth()/6)*5)+gameBoard.getLeft();
        yCords[21]=((gameBoard.getHeight()/6)*5)+gameBoard.getTop();
        //Rad6
        xCords[22]=((gameBoard.getWidth()/6)*0)+gameBoard.getLeft();
        yCords[22]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
        xCords[23]=((gameBoard.getWidth()/6)*3)+gameBoard.getLeft();
        yCords[23]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
        xCords[24]=((gameBoard.getWidth()/6)*6)+gameBoard.getLeft();
        yCords[24]=((gameBoard.getHeight()/6)*6)+gameBoard.getTop();
         
  }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			restartGame=true;
			myPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        	SharedPreferences.Editor editor=myPreferences.edit();
    		editor.putBoolean(IS_SAVED, false);
    		editor.commit();
			Intent i;
			i=new Intent(this,Gameboard.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
	
