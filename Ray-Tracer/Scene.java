
import java.util.*;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;

public class Scene implements SceneObjectsInterface 
{
	enum tParts { BACKGROUND_COL, BACKGROUND_TEX, AMBIENT_LIGHT, SUPER_SAMP_WIDTH, UNKNOWN };	
	Map<String, tParts> stringToPart;

	List<SceneGeometricPrimitive> m_sceneObjectsArr = new ArrayList<SceneGeometricPrimitive>();
	List<Light> m_lightSourcesList = new ArrayList<Light>();
	Camera m_cameraObject = null;
	
	double[] m_bgColor = { 0, 0, 0 };	
	double[] m_ambientIntensity = { 0, 0, 0 };	
	String m_bgText;
	double m_superSampWidth = 1;
	
	int m_textureWidth = 0, m_textureHeight = 0;
	double[][][] backgroundTexture = null;		
	int m_canvasWidth = 0, m_canvasHeight = 0;
	
	public void initObject() 
	{			
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("background-col", tParts.BACKGROUND_COL);
		stringToPart.put("background-tex", tParts.BACKGROUND_TEX);
		stringToPart.put("ambient-light", tParts.AMBIENT_LIGHT);
		stringToPart.put("super-samp-width", tParts.SUPER_SAMP_WIDTH);
		
		m_sceneObjectsArr = new ArrayList<SceneGeometricPrimitive>();
		m_lightSourcesList = new ArrayList<Light>();
	}
	
	public boolean setParameter(Parameter param) throws Exception 
	{	
		tParts part = stringToPart.get(param.getName());
				
		switch (part)
		{
			case BACKGROUND_COL:
				m_bgColor = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case BACKGROUND_TEX:
				m_bgText = param.getValueString();
				backgroundTexture = loadTexture(m_bgText);
				m_textureWidth = GetCanvasWidth();
				m_textureHeight = GetCanvasHeight(); 
				
				break;
			case AMBIENT_LIGHT:
				m_ambientIntensity = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case SUPER_SAMP_WIDTH:
				m_superSampWidth = Double.parseDouble(param.getValueString());
				break;				
				
			default:
				System.out.println("Error [Camera::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}		
	
	public void addObject(SceneObjectsInterface object) throws Exception 
	{								
		// Add physicalObject
		if (object instanceof SceneGeometricPrimitive) {
			m_sceneObjectsArr.add((SceneGeometricPrimitive)object);
			return;
		}
		
		if (object instanceof Camera) {
			this.m_cameraObject = (Camera)object;															
			return;
		} 
		
		if (object instanceof Light) {
			this.m_lightSourcesList.add((Light)object);
			return;
		}
						
		throw new Exception("Error [Scene::addObject] : Unkonwn object type");
	}
	
	void SetCanvasSize(int width, int height)
	{
		this.m_canvasWidth = width;
		this.m_canvasHeight = height;
	}
	
	public double[] getBackgroundAt(int x, int y) {
		if (backgroundTexture != null) {
			int textureX = (int)Math.round((double)x / GetCanvasWidth() * m_textureWidth); 
			int textureY = (int)Math.round((double)y / GetCanvasHeight() * m_textureHeight);			
			return backgroundTexture[textureY][textureX];
		}
		else {
			return GetBackgroundColor();
		}
			
	}
	
	public Camera GetCamera() {
		return m_cameraObject;
	}
	public void SetCamera(Camera camera) {
		this.m_cameraObject = camera;
	}
	
	public List<SceneGeometricPrimitive> GetPrimitives() {
		return m_sceneObjectsArr;
	}
	public void SetPrimitives(List<SceneGeometricPrimitive> listOfPrimitives) {
		this.m_sceneObjectsArr = listOfPrimitives;
	}
	public double[] GetBackgroundColor() {
		return m_bgColor;
	}
	public void SetBackgroundColor(double[] backgroundColor) {
		this.m_bgColor = backgroundColor;
	}
	public double[] GetAmbientLight() {
		return m_ambientIntensity;
	}
	public void SetAmbientLight(double[] ambientLight) {
		this.m_ambientIntensity = ambientLight;
	}
	
	public int GetSuperSampleWidth() {
		return (int)m_superSampWidth;
	}
	public void SetSuperSampleWidth(int superSampleWidth) {
		this.m_superSampWidth = superSampleWidth;
	}
	
	public List<Light> GetLights() {
		return m_lightSourcesList;
	}

	public void SetLights(List<Light> lights) {
		this.m_lightSourcesList = lights;
	}

	public void setCanvasSize(int height, int width) 
	{
		this.m_canvasHeight = height;
		this.m_canvasWidth = width;
	}			
	
	public int GetCanvasHeight() {
		return m_canvasHeight;
	}

	public void SetCanvasHeight(int height) {
		this.m_canvasHeight = height;
	}

	public int GetCanvasWidth() {
		return m_canvasWidth;
	}

	public void SetCanvasWidth(int width) {
		this.m_canvasWidth = width;
	}

	public void Finalize() {
		
	}	
	
	
	public double[][][] loadTexture(String textureFileName) 
	{				
		ImageLoader imageLoader = new ImageLoader();
		ImageData[] imageDataArr = imageLoader.load(textureFileName);
		ImageData imageData = imageDataArr[0];
		imageData = imageData.scaledTo(GetCanvasWidth(), GetCanvasHeight());
		int textureWidth = GetCanvasWidth();
		int textureHeight = GetCanvasHeight();
		
		double[][][] texture = new double[textureHeight][textureWidth][3];
		
		for (int i = 0; i < textureHeight; i++) {
			for (int j = 0; j < textureWidth; j++) {
				int pixel = imageData.getPixel(j, i);
				RGB rgb = imageData.palette.getRGB(pixel);
				
				texture[i][j][0] = rgb.red / 255F;
				texture[i][j][1] = rgb.green / 255F;
				texture[i][j][2] = rgb.blue / 255F;
			}
		}
		
		return texture;						
	}

	
}
