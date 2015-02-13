import java.io.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class RayTracer {

	public final double EPSILON = 0.00000001F;	
	
	static Scene m_scene;
	private Camera m_camera;
	private double m_pixelWidth;
	private double m_pixelHeight;
	private int m_superSampleWidth;

	private static String m_workingDirectory;
	static Display display = null;
	
	public static String GetWorkingDirectory() {
		return m_workingDirectory;
	}
	
	
	public static void main(String[] args) 
	{
		display = new Display();
		RayTracer tracer = new RayTracer();
		tracer.runMain(display);
		display.dispose();
	}
	
	public Ray constructRayThroughPixel(int x, int y, double sampleXOffset, double sampleYOffset) {										 																
		Ray ray = new Ray(m_camera.GetEye() , m_camera.GetDirection(), m_camera.GetScreenDist());
		double[] endPoint = ray.getEndPoint();				
		double upOffset = -1 * (y - (m_scene.GetCanvasHeight() / 2) - (sampleYOffset /m_scene.GetSuperSampleWidth())) * m_pixelHeight;
		double rightOffset = (x - (m_scene.GetCanvasWidth() / 2) + (sampleXOffset / m_scene.GetSuperSampleWidth())) * m_pixelWidth;		
		MathCalcsLib.addVectorAndMultiply(endPoint, m_camera.GetViewplaneUp(), upOffset);
		MathCalcsLib.addVectorAndMultiply(endPoint, m_camera.GetRightDirection(), rightOffset);				
		ray.setDirection(MathCalcsLib.calcPointsDiff(m_camera.GetEye(), endPoint));						
		ray.normalize();
		return ray;
	}
	
	// Finds the primitive we are currently intersecting with
	public RayIntersection findIntersection(Ray ray, SceneGeometricPrimitive ignorePrimitive)
	{		
		double minDistance = Double.POSITIVE_INFINITY;
		SceneGeometricPrimitive minPrimitive = null;
		
		for(SceneGeometricPrimitive primitive : m_scene.GetPrimitives())
		{
			double dist = primitive.FindIntersection(ray);

			if (dist < minDistance && dist > EPSILON && primitive != ignorePrimitive)
			{
				minPrimitive = primitive;
				minDistance = dist;
			}
		}		
		return new RayIntersection(minDistance, minPrimitive);
	}
	
	void addAmbient(Surface surface, double[] color)
	{
		double[] sceneAmbient = m_scene.GetAmbientLight(); 
		double[] surfaceAmbient = surface.getAmbient();
		
		color[0] += sceneAmbient[0] * surfaceAmbient[0]; 		
		color[1] += sceneAmbient[1] * surfaceAmbient[1];
		color[2] += sceneAmbient[2] * surfaceAmbient[2];		
	}
	
	void addEmission(Surface surface, double[] color)
	{
		double[] surfaceEmission = surface.getEmission();	
		color[0] += surfaceEmission[0];
		color[1] += surfaceEmission[1];
		color[2] += surfaceEmission[2];				
	}
	
	void addDiffuse(double[] diffuse, double[] amountOfLightAtIntersection, double visibleDiffuseLight, double[] color)
	{
		color[0] += diffuse[0] * amountOfLightAtIntersection[0] * visibleDiffuseLight;
		color[1] += diffuse[1] * amountOfLightAtIntersection[1] * visibleDiffuseLight;
		color[2] += diffuse[2] * amountOfLightAtIntersection[2] * visibleDiffuseLight;
	}
	
	public double[] getIntersectionColor(Ray ray, RayIntersection intersection, int recursionDepth)
	{
		SceneGeometricPrimitive primitive = intersection.getPrimitive();		
		if (primitive == null) return m_scene.GetBackgroundColor();		
		Surface surface = primitive.getSurface();			
		double[] color = new double[3];		
		
		ray.setMagnitude(intersection.getDistance());		
		double[] pointOfIntersection = ray.getEndPoint();								
		double[] diffuse = primitive.getColorAt(pointOfIntersection);
		if (diffuse==null) {
			System.err.print("Error: Diffuse is null");
		}		
		ray.setMagnitude(intersection.getDistance() - 1);
		double[] normal = primitive.GetNormal(pointOfIntersection);		
		
		for (Light light: m_scene.GetLights()) 
		{
			double[] vectorToLight = light.getVectorToLight(pointOfIntersection);			
			
			Ray rayToLight = new Ray(pointOfIntersection, vectorToLight, 1);
			rayToLight.normalize();
						
			double distanceToBlockingPrimitive = findIntersection(rayToLight, null).getDistance();
			double distanceToLight = MathCalcsLib.norm(MathCalcsLib.calcPointsDiff(pointOfIntersection, light.getPosition()));
			
			boolean lightVisible = distanceToBlockingPrimitive <= EPSILON || distanceToBlockingPrimitive >= distanceToLight;
				
			if (lightVisible) 
			{								
				double[] amountOfLightAtIntersection = light.getAmountOfLight(pointOfIntersection);								
				
				double visibleDiffuseLight = MathCalcsLib.dotProduct(vectorToLight, normal);								
				if (visibleDiffuseLight > 0) {
					addDiffuse(diffuse, amountOfLightAtIntersection, visibleDiffuseLight, color);
				}							
			}
		}				
		addAmbient(surface, color);
		addEmission(surface, color);
				
		return color;
	}
	
	void renderTo(ImageData dat, Canvas canvas) throws Parser.ParseException
	{
		m_scene = new Scene();		
		m_scene.setCanvasSize(dat.height, dat.width);
		
		try {
			SceneParser f = new SceneParser(m_scene);
			f.parse(new StringReader(m_sceneText.getText()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_camera = m_scene.GetCamera();
		m_superSampleWidth = m_scene.GetSuperSampleWidth();
		m_pixelWidth = m_camera.GetScreenWidth() / m_scene.GetCanvasWidth();
		m_pixelHeight = m_scene.GetCanvasWidth() / m_scene.GetCanvasHeight() * m_pixelWidth;
		GC gc = new GC(canvas);
		gc.fillRectangle(m_rect);
		
		for(int y = 0; y < dat.height; ++y)
		{			
			for(int x = 0; x < dat.width; ++x)
			{									
				int hits = 0;
				double[] color = new double[3];								
				
				for (int k = 0; k < m_superSampleWidth; k++) 
				{															
					for (int l = 0; l < m_superSampleWidth; l++) 
					{					
						double[] sampleColor = null;
						Ray ray = constructRayThroughPixel(x, y, k, l);
						
						RayIntersection intersection = findIntersection(ray, null);
						
						if (null != intersection.getPrimitive()) {
							hits++;
							try {
								sampleColor = getIntersectionColor(ray, intersection, 1);
							}
							catch (Exception e) { }
							MathCalcsLib.addVector(color, sampleColor);
																		
							ray.setMagnitude(intersection.getDistance());																																						
						}
					}					
				}
				
				if (hits == 0) {
					color = m_scene.getBackgroundAt(x, y);			
				}	
				else {
					MathCalcsLib.multiplyVectorByScalar(color, 1F / hits);
				}
				Color cc = floatArrayToColor(color);				
				
				dat.setPixel(x, y, floatArrayToColorInt(color));
				
				gc.setForeground(cc);
				gc.drawPoint(x, y);
				m_imgdat.setPixel(x,y,m_imgdat.palette.getPixel(cc.getRGB()));
				cc.dispose(); 
			}
		}			
	}
	
	
    public static String readTextFile(Reader in) throws IOException
    {
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader reader = new BufferedReader(in);
       
        char[] chars = new char[1024];
        int numRead;
        while((numRead = reader.read(chars)) > -1){
            sb.append(String.valueOf(chars, 0, numRead));
        }
        return sb.toString();
    }
	
	
	void openFile(String filename)
	{
		try {
			m_workingDirectory = new File(filename).getParent() + "\\";
			Reader fr = new FileReader(filename);
			m_sceneText.setText(readTextFile(fr));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	
	org.eclipse.swt.graphics.Rectangle m_rect;
	ImageData m_imgdat;

	Text m_sceneText;
	
	
	void runMain(final Display display)
	{
		Shell editShell = new Shell(display);
		editShell.setText("Input");
		editShell.setSize(300, 550);
		GridLayout gridEdit = new GridLayout();
		editShell.setLayout(gridEdit);
		 
		Composite editComp = new Composite(editShell, SWT.NONE);
		GridData ld = new GridData();
		ld.heightHint = 30;
		editComp.setLayoutData(ld);
		
		m_sceneText = new Text(editShell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		ld = new GridData(GridData.FILL_BOTH);
		m_sceneText.setLayoutData(ld);
		Font fixed = new Font(display, "Courier New", 10, 0);
		m_sceneText.setFont(fixed);
		
		
		final Shell shell = new Shell(display);
		shell.setText("Ray Tracer Ex");
		shell.setSize(600, 500);
		GridLayout gridLayout = new GridLayout();
		
		shell.setLayout(gridLayout);
		
		final Canvas canvas = new Canvas(shell, SWT.BORDER | SWT.NO_REDRAW_RESIZE);
		ld = new GridData(GridData.FILL_BOTH);
		canvas.setLayoutData(ld);

		Composite comp = new Composite(shell, SWT.NONE);
		ld = new GridData();
		ld.heightHint = 45;
		comp.setLayoutData(ld);
		
		Button renderBot = new Button(comp, SWT.PUSH);
		renderBot.setText("Render");
		renderBot.setSize(150, 40);

	
		renderBot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent ev) 
			{
				try {
					m_imgdat = new ImageData(m_rect.width, m_rect.height, 24, new PaletteData(0xFF0000 , 0xFF00 , 0xFF));
					renderTo(m_imgdat, canvas);
				} catch (Parser.ParseException e) {
					System.out.println("Error Parsing text: " + e.getMessage());
				}					
			}
			});


		Button savePngBot = new Button(comp, SWT.PUSH );
		savePngBot.setText("Save PNG");
		savePngBot.setBounds(250, 0, 70, 40);
		savePngBot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent ev)
			{
				FileDialog dlg = new FileDialog(shell, SWT.SAVE);
				dlg.setText("Save PNG");
				dlg.setFilterExtensions(new String[] { "*.png", "*.*" });
				String selected = dlg.open();
				if (selected == null)
					return;

			    ImageLoader loader = new ImageLoader();
			    loader.data = new ImageData[] { m_imgdat };
			    loader.save(selected, SWT.IMAGE_PNG);
			}
		});
		

		Button openBot = new Button(editComp, SWT.PUSH);
		openBot.setText("Open");
		openBot.setBounds(0, 0, 100, 30);
		
		openBot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setText("Open Model");
				dlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
				String selected = dlg.open();
				if (selected != null)
					openFile(selected);
				
			}
		});


		canvas.addListener (SWT.Resize, new Listener() {
		    public void handleEvent(Event e) {
		       m_rect = canvas.getClientArea();
		    }
		  });		

		canvas.addPaintListener(new PaintListener() 
		{
			public void paintControl(PaintEvent e) 
			{
				GC gc = e.gc;
				if (m_imgdat == null)
				{
					gc.drawLine(0, 0, e.width, e.height);
					return;
				}
				Image img = new Image(display, m_imgdat);
				if (img != null)
				{
					gc.drawImage(img, 0, 0);
				}
				img.dispose();
			}
		});

		shell.open();
		editShell.open();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		

	
	}
	
	public static int floatArrayToColorInt(double[] color) {
		int colorInt = Math.min(255, (int)Math.round(color[0] * 255)) << 16 & 0xFF0000 |
					   Math.min(255, (int)Math.round(color[1] * 255)) << 8 & 0xFF00 |
					   Math.min(255, (int)Math.round(color[2] * 255));
						
		return colorInt;
	}
	public static Color floatArrayToColor(double[] color) {
		int r = Math.min(255, (int)Math.round(color[0] * 255));
		int g = Math.min(255, (int)Math.round(color[1] * 255));
		int b = Math.min(255, (int)Math.round(color[2] * 255));
						
		return new Color(Display.getDefault(), r, g, b);
	}

}

