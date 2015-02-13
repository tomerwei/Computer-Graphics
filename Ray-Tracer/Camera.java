import java.util.HashMap;
import java.util.Map;

public class Camera implements SceneObjectsInterface 
{
	enum tParts { EYE, LOOK_AT, DIRECTION, UP_DIRECTION, SCREEN_DIST, SCREEN_WIDTH, UNKNOWN };	
	Map<String, tParts> stringToPart;
	
	private double[] m_eye = {0,0,0};
	private double[] m_lookAt = {0,0,0};
	private double[] m_direction = null;
	private double[] m_upDirection = {0,0,0};	
	private double m_screenDist = 1;
	private double m_screenWidth = 2;	
	
	private double[] m_rightDirection = {0,0,0};
	private double[] m_viewplaneUp = {0,0,0}; 
	
	public void initObject() 
	{
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("eye", tParts.EYE);
		stringToPart.put("look-at", tParts.LOOK_AT);
		stringToPart.put("direction", tParts.DIRECTION);
		stringToPart.put("up-direction", tParts.UP_DIRECTION);
		stringToPart.put("screen-dist", tParts.SCREEN_DIST);
		stringToPart.put("screen-width", tParts.SCREEN_WIDTH);		
	}
	
	public void Finalize()
	{
		if (null == m_direction)
		{			
			m_direction = MathCalcsLib.calcPointsDiff(m_eye, m_lookAt);
			MathCalcsLib.normalize(m_direction);						
		}
		
		m_rightDirection = MathCalcsLib.crossProduct(m_upDirection, m_direction);
		MathCalcsLib.normalize(m_rightDirection);
		MathCalcsLib.multiplyVectorByScalar(m_rightDirection, -1);
		
		m_viewplaneUp = MathCalcsLib.crossProduct(m_rightDirection, m_direction);		
		MathCalcsLib.normalize(m_viewplaneUp);							
	}

	public boolean setParameter(Parameter param) throws Exception 
	{
		tParts part = stringToPart.get(param.getName());
		
		switch (part)
		{
			case EYE:
				m_eye = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case LOOK_AT:
				m_lookAt = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case DIRECTION:
				m_direction = SceneParser.StringArrToDoubleArr(param.getValueArray());
				MathCalcsLib.normalize(m_direction);
				break;
			case UP_DIRECTION:
				m_upDirection = SceneParser.StringArrToDoubleArr(param.getValueArray());
				MathCalcsLib.normalize(m_upDirection);
				break;				
			case SCREEN_DIST:
				m_screenDist = Double.parseDouble(param.getValueString());
				break;
			case SCREEN_WIDTH:
				m_screenWidth = Double.parseDouble(param.getValueString());
				break;
				
			default:
				System.out.println("Error [Camera::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}
		return true;
	}
	
	public double[] GetEye() {
		return m_eye;
	}
	public void SetEye(double[] eye) {
		this.m_eye = eye;
	}
	public double[] GetLookAt() {
		return m_lookAt;
	}
	public void SetLookAt(double[] lookAt) {
		this.m_lookAt = lookAt;
	}
	public double GetScreenDist() {
		return m_screenDist;
	}
	public void SetScreenDist(double screenDist) {
		this.m_screenDist = screenDist;
	}
	public double GetScreenWidth() {
		return m_screenWidth;
	}
	public void SetScreenWidth(double screenWidth) {
		this.m_screenWidth = screenWidth;
	}
	
	public double[] GetUpDirection() {
		return m_upDirection;
	}
	public void SetUpDirection(double[] upDirection) {
		this.m_upDirection = upDirection;
	}
	
	public double[] GetRightDirection() {
		return m_rightDirection;
	}
	public void SetRightDirection(double[] rightDirection) {
		this.m_rightDirection = rightDirection;
	}	
	
	public double[] GetDirection() {
		return m_direction;
	}

	public void SetDirection(double[] direction) {
		this.m_direction = direction;
	}


	public double[] GetViewplaneUp() {
		return m_viewplaneUp;
	}


	public void SetViewplaneUp(double[] viewplaneUp) {
		this.m_viewplaneUp = viewplaneUp;
	}		

}
