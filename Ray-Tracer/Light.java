
import java.util.HashMap;
import java.util.Map;

public abstract class Light implements SceneObjectsInterface 
{
	protected enum tParts { COLOR,  //related to all Light objects 
		DIRECTION, //related to directed Light objects
		POS,ATTENUATION, //related to LightPoint 
		P0, P1, P2, GRIDWIDTH, //related to LightArea 
		UNKNOWN }; 
		
	protected Map<String, tParts> stringToPart;	
	protected double[] m_color = {1.0,1.0,1.0};
	protected double[] m_pos;
	
	public void initObject() 
	{
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("color", tParts.COLOR);
	}
	
	public abstract double[] getAmountOfLight(double[] point);
	
	public abstract double[] getVectorToLight(double[] pointOfIntersection);

	public boolean setParameter(Parameter param) throws Exception 
	{			
		tParts part = stringToPart.get(param.getName());		
		switch (part)
		{
			case COLOR:
				m_color = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;				
			default:				
				return false;
		}
		return true;
	}

	public double[] getPosition() {
		return m_pos;
	}
	
	public void setPosition(double[] position) {
		this.m_pos = position;
	}
	
	public double[] getColor() {
		return m_color;
	}
	
	public void setColor(double[] color) {
		this.m_color = color;
	}

	public void Finalize() {
		
	}	
}
