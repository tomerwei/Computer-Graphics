import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
 
public class renderer implements GLEventListener, KeyListener {
    float rotateT = 0.0f;
    float _width, _height;
    
    private float rquad = 0.0f;
    private boolean rqadBool =false;
    private float rtri = 0.0f;
    private GLUT glut= new GLUT();;
    private int lightState = 0;
    private final float roomDimMultiplier = 1.5f;
    private final float cubeDimMultiplier = 0.5f;
    private int renderMode = 0;   
    private boolean isDisplayListInit = false;
    private String path;
    
    public renderer(String path) {
    	this.path= path;
    }
    
    
    static GLU glu = new GLU(); 
    static GLCanvas canvas = new GLCanvas(); 
    static Frame frame = new Frame("Jogl Quad drawing"); 
    static Animator animator = new Animator(canvas);
    GL2 gl = null;
    GLModel model1 = null;

 private void addObj()
        {    	
             gl.glEnable( GL.GL_TEXTURE_2D );
             gl.glShadeModel( GL2.GL_SMOOTH );
             String path1 = path;
             try
             {
                 FileInputStream r_path1 = new FileInputStream(path1);
                 BufferedReader b_read1 = new BufferedReader(new InputStreamReader(r_path1));
                 model1 = new GLModel(b_read1, true, null, gl, (float)0.01);
                 //model1 = new GLModel(b_read1, true, "models/formula.mtl", gl);
                 r_path1.close();
                 b_read1.close();
             }    
    	    catch( Exception e ){
    	            System.out.println("LOADING ERROR" +  e);
    	    }
    
        }

     private void draw() {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            addRoom();           
            float backWallAmbient[] = {0.19f, 0.59f, 0.39f};
        	float backWallDiffuse[] = {0.9f, 0.9f, 0.35f};            
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
            
            gl.glRotatef(rquad, 1.0f, 1.0f, 0.0f);
            
            if(null==model1) addObj();
            model1.opengldraw(gl, renderMode);
            if(rqadBool) rquad+=0.5;
          	_width = 1000;
        	_height = 2000;
            setCamera(7f); 	
     }

        
    private void addRoom() {
    	if(0 == renderMode%3) {
    		addRoomBruteForce();
    	}
    	else if(1 == renderMode%3) {
    		addRoomDisplayList();
    	}    	    	
    	else if(2== renderMode%3) {
    		roomVertrexArray();
    	}
    }
    
    private void roomVertrexArray()
    {
        float vertices1[] = new float[]    		  
        	      { //floor
        	    		  -1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier, 
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  -1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	   		//backwall	  
        	    		  -1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  
              	   	// topwall	  
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier
        	    	//hidden wall	  
        	    		  /*
        	    		  ,-1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier
*/
        	    		  
        	      };  
      	      	
        	      FloatBuffer tmpVertices1Buf = Buffers.newDirectFloatBuffer(vertices1.length);
        	      for (int i = 0; i < vertices1.length; i++) {
        	        tmpVertices1Buf.put(vertices1[i]);
        	      }
        	      
        	      float vertices2[] = new float[]    		  
        	      { //left wall
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier, 
        	    		  -1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  -1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier,    		       
        	    		  -1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier    		 
        	      };    	     
        	      FloatBuffer tmpVertices2Buf = Buffers.newDirectFloatBuffer(vertices2.length);
        	      for (int i = 0; i < vertices2.length; i++) {
        	        tmpVertices2Buf.put(vertices2[i]);
        	      }      
        	      
        	      float vertices3[] = new float[]    		  
        	      { //right wall
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier,
        	    		  1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier,
        	    		  1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier    		  
        	      };    	     
        	      
        	      FloatBuffer tmpVertices3Buf = Buffers.newDirectFloatBuffer(vertices3.length);
        	      for (int i = 0; i < vertices3.length; i++) {
        	        tmpVertices3Buf.put(vertices3[i]);
        	      }
        	      
        	      float roomDimMultiplierX = roomDimMultiplier/4;
        	      float vertices4[] = new float[]
        	      {
          	    	//hole
    	    		  -1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX,
    	    		  1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX,
    	    		  1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX,
    	    		  -1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX        	    		          	    	  
        	      };
        	      FloatBuffer tmpVertices4Buf = Buffers.newDirectFloatBuffer(vertices4.length);
        	      for (int i = 0; i < vertices4.length; i++) {
        	        tmpVertices4Buf.put(vertices4[i]);
        	      }
        	      
        	      tmpVertices1Buf.rewind();
        	      tmpVertices2Buf.rewind();      
        	      tmpVertices3Buf.rewind();
        	      tmpVertices4Buf.rewind();
     
    	
      gl.glEnableClientState (GL2.GL_VERTEX_ARRAY);              
  	float backWallAmbient[] = {0.99f, 0.89f, 0.39f};
  	float backWallDiffuse[] = {0.9f, 0.79f, 0.35f};
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);        	      
      gl.glVertexPointer(3, GL2.GL_FLOAT, 0, tmpVertices1Buf);      
      gl.glDrawArrays(GL2.GL_QUADS, 0, vertices1.length);
      
  	float leftWallAmbient[] = {0.50f, 0f, 0.0f};
  	float leftWallDiffuse[] = leftWallAmbient;
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, leftWallAmbient,0);
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, leftWallDiffuse,0);
      gl.glVertexPointer(3, GL2.GL_FLOAT, 0, tmpVertices2Buf);      
      gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
      
  	float rightWallAmbient[] = {0.0f, 0.50f, 0.0f};
  	float rightWallDiffuse[] = rightWallAmbient;
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, rightWallAmbient,0);
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, rightWallDiffuse,0);        	  	
      gl.glVertexPointer(3, GL2.GL_FLOAT, 0, tmpVertices3Buf);      
      gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
      
  	float backHoleAmbient[] = {0.5f, 0.5f, 0.5f};
	float backHoleDiffuse[] = {1f, 1f, 1f};        	
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backHoleAmbient,0);
  	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backHoleDiffuse,0);        	  	
      gl.glVertexPointer(3, GL2.GL_FLOAT, 0, tmpVertices4Buf);      
      gl.glDrawArrays(GL2.GL_QUADS, 0, 8);      
    }
    
    private void addRoomBruteForce() {
        //float second [] = (0.98f, 0.92f, 0.84f);        
        gl.glBegin(GL2.GL_QUADS);
        	/* Floor */        
        	float backWallAmbient[] = {0.99f, 0.89f, 0.39f};
        	float backWallDiffuse[] = {0.9f, 0.79f, 0.35f};
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);        	
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
        	
        	/* Walls */
        	/* Top Wall */        	
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
        	        	        	
        	// Left Wall
        	float leftWallAmbient[] = {0.50f, 0.0f, 0.0f};
        	float leftWallDiffuse[] = leftWallAmbient;
        	//ambientColor	0.5 0.0 0.0
        	//diffuseColor	0.5 0.0 0.0
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, leftWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, leftWallDiffuse,0);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	
        	// Right Wall
        	float rightWallAmbient[] = {0.0f, 0.50f, 0.0f};
        	float rightWallDiffuse[] = rightWallAmbient;
        	//ambientColor	0.0 0.5 0.0
        	//diffuseColor	0.0 0.5 0.0        	
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, rightWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, rightWallDiffuse,0);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	
        	// Back wall
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
        	
         	/* Hole */
        	float backHoleAmbient[] = {0.5f, 0.5f, 0.5f};
        	float backHoleDiffuse[] = {1f, 1f, 1f};        	
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backHoleAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backHoleDiffuse,0);        	        	
        	float roomDimMultiplierX = roomDimMultiplier/4;
        	gl.glVertex3f(-1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX);
        	gl.glVertex3f(1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX);        	
        	gl.glVertex3f(1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX);
        	gl.glVertex3f(-1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX);

        	// Hidden wall
        	/*
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
        	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,4*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,4*roomDimMultiplier);
        	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,4*roomDimMultiplier);
        	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,4*roomDimMultiplier);
        	*/
			  
        gl.glEnd();
    }
               
    
    private void initDisplayList() {
       	float backWallAmbient[] = {0.79f, 0.79f, 0.19f};
       	float backWallDiffuse[] = {0.9f, 0.9f, 0.35f};
       	float rightWallAmbient[] = {0.0f, 0.50f, 0.0f};
       	float rightWallDiffuse[] = rightWallAmbient;


       gl.glNewList(2, GL2.GL_COMPILE);
       gl.glBegin(GL2.GL_QUADS);          
       
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);        	
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
   	
   	/* Walls */
   	/* Top Wall */        	
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
   	        	        	
   	// Left Wall
   	float leftWallAmbient[] = {0.50f, 0.0f, 0.0f};
   	float leftWallDiffuse[] = leftWallAmbient;
   	//ambientColor	0.5 0.0 0.0
   	//diffuseColor	0.5 0.0 0.0
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, leftWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, leftWallDiffuse,0);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	
   	// Right Wall
   	//ambientColor	0.0 0.5 0.0
   	//diffuseColor	0.0 0.5 0.0        	
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, rightWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, rightWallDiffuse,0);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,2*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	
   	// Back wall
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,-1*roomDimMultiplier);
   	
	/* Hole */
	float backHoleAmbient[] = {0.5f, 0.5f, 0.5f};
	float backHoleDiffuse[] = {1f, 1f, 1f};        	
	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backHoleAmbient,0);
	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backHoleDiffuse,0);        	        	
	float roomDimMultiplierX = roomDimMultiplier/4;
	gl.glVertex3f(-1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX);
	gl.glVertex3f(1*roomDimMultiplierX,(float)4*roomDimMultiplierX,-1*roomDimMultiplierX);        	
	gl.glVertex3f(1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX);
	gl.glVertex3f(-1*roomDimMultiplierX,(float)3.9*roomDimMultiplierX,1*roomDimMultiplierX);

   	// Hidden wall
   	/*
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, backWallAmbient,0);
   	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, backWallDiffuse,0);
   	gl.glVertex3f(-1*roomDimMultiplier,-1*roomDimMultiplier,4*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,-1*roomDimMultiplier,4*roomDimMultiplier);
   	gl.glVertex3f(1*roomDimMultiplier,1*roomDimMultiplier,4*roomDimMultiplier);
   	gl.glVertex3f(-1*roomDimMultiplier,1*roomDimMultiplier,4*roomDimMultiplier);        	        	        	        
	*/	
       gl.glEnd();
       gl.glEndList();
    }
    
    private void addRoomDisplayList()
    {
    	if(!isDisplayListInit) {
    		initDisplayList();
    		isDisplayListInit= true;
    	}
       gl.glCallList(2);
    }
    
    public void display(GLAutoDrawable gLDrawable) 
    {    	 
        gl = gLDrawable.getGL().getGL2();        
        lightChange();        
        draw();
    }
    
    private void setLight2() {    	
    	   //float ambient[] = { 0.1f, 0.1f, 0.1f, 1.0f };
    	   //float diffuse[] = { 1.0f, 1.0f, 1.0f, 0.0f };
    	   //float specular[] = { 0.7f, 1f, 0.22f, 0.0f };
    	   float position[] = {roomDimMultiplier/2, roomDimMultiplier-0.005f, -roomDimMultiplier/2, roomDimMultiplier/4 };
    	   float spotDirection[] = { 0f, -1f, 0f };
    	   float lmodel_ambient[] = { 0.4f, 0.4f, 0.4f, 1.0f };
    	   float local_view[] = { 0.0f };
    	   gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    	   gl.glEnable(GL2.GL_DEPTH_TEST);
    	   gl.glShadeModel(GL2.GL_SMOOTH);
    	   //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);    	  
    	   //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
    	   gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
    	   gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, spotDirection, 0);
    	   gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF, 55.0f);    	   
    	   //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);
    	   //gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient,0);
    	   //gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view,0);    	       	  
    	   gl.glEnable(GL2.GL_LIGHTING);
    	   gl.glEnable(GL2.GL_LIGHT0);
    }
    
    private void setCamera(float distance) {
        // Change to projection matrix.
    	
    	
    	
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        

        // Perspective.
        float widthHeightRatio = (float) _height / (float) _width;
        glu.gluPerspective(33, widthHeightRatio, 1, 1000);
        //gl.glFrustum (0,0,0,0, 1, 400000); 
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    
 private void lightChange() {
    	
    	float position[] = null, position2[] = null;
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    	if(0==lightState%3) 
    	{
    		position = new float[]{(float)0,(float)-0.9,(float)0,(float)0.9}; // from-up
    		position2 = new float[]{(float)0,(float)0.9,(float)0,(float)0.9}; // from-down
	 	
    	   float diffuse[] = { 0.8f, 0.8f, 0.8f, 1.0f };
    		
          gl.glEnable(GL2.GL_DEPTH_TEST);
     	  gl.glShadeModel(GL2.GL_SMOOTH);
     	   
     	  gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
     	  gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
     	   	
     	  gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
     	  gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position2, 0);
     	  	     	  	
     	   
     	  gl.glEnable(GL2.GL_DEPTH_TEST);
     	  gl.glShadeModel(GL2.GL_SMOOTH);
     	  gl.glEnable(GL2.GL_LIGHTING);
     	  gl.glEnable(GL2.GL_LIGHT0);
     	  gl.glEnable(GL2.GL_LIGHT1);
     	  return;
    	}
    	else if(1==lightState%3) {    		
    		position = new float[]{roomDimMultiplier/2, 10*roomDimMultiplier-0.005f, -(roomDimMultiplier/2) , 0 };
    	}
    	else if(2==lightState%3) {    		
    		position = new float[]{0, 0, -1f, 0 };    	   
    	}
 
 	   	gl.glShadeModel(GL2.GL_SMOOTH);
 	   	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0); 	   
 	   	gl.glEnable(GL2.GL_LIGHTING);
 	   	gl.glEnable(GL2.GL_LIGHT0);
    }
 
    
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }
 
    public void init(GLAutoDrawable gLDrawable) {
        GL2 gl = gLDrawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        ((Component) gLDrawable).addKeyListener(this);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
    	//_width = width;
    	//_height = height;
    	
        GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }
        float h = (float) width / (float) height;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(50.0f, h, 1.0, 1000.0);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
 
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            exit();
        }
        
        switch (e.getKeyCode()) {
        case KeyEvent.VK_I:
        	lightState++;
        	if(0==lightState%3) {
        		System.out.println("Point Light on celling");
        	}
        	else if(1==lightState%3) {
        		System.out.println("Direct Light from above");
        	}
        	else if(2==lightState%3) {
        		System.out.println("Spotlight from behind the camera");
        	}
        	break;
        	
        case KeyEvent.VK_R:
        	renderMode++;
        	if(0==renderMode%3) {
        		System.out.println("Rendering using brute force");
        	}
        	else if(1==renderMode%3) {
        		System.out.println("Rendering using display lists");
        	}
        	else if(2==renderMode%3) {
        		System.out.println("Rendering using vertex arrays");
        	}
        	break;
        case KeyEvent.VK_SPACE:
        	if(rqadBool) {
        		rqadBool = false;
        	}
        	else {
        		rqadBool = true;
        	}        	
        	break;
        	
        default:
          break;
      }
    }
 
    public void keyReleased(KeyEvent e) {
    }
 
    public void keyTyped(KeyEvent e) {
    }
 
    public static void exit() {
        animator.stop();
        frame.dispose();
        System.exit(0);
    }
 
    public static void main(String[] args) {
        if(1 != args.length) {
        	System.out.println("Error: wrong number of arguments. Please enter only 1");
        	System.exit(1);
        }
    	canvas.addGLEventListener(new renderer(args[0]));
        frame.add(canvas);
        frame.setSize(640, 480);
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });     
        frame.setVisible(true);
        animator.start();
        canvas.requestFocus();
    }    

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
        

}