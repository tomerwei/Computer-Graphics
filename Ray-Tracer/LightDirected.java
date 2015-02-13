
public class LightDirected extends Light 
{
	double[] m_direction = {0,0,0};
	private double[] oppositeDirection = {0,0,0};

	public void initObject() 
	{
		super.initObject();
		this.m_pos = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };;		
		stringToPart.put("direction", Light.tParts.DIRECTION);		
		stringToPart.put("pos", Light.tParts.POS);		
	}
	
	public boolean setParameter(Parameter param) throws Exception 
	{
		if (super.setParameter(param)) return true;
		
		tParts part = stringToPart.get(param.getName());
		
		switch (part)
		{
			case DIRECTION:
				this.m_direction = SceneParser.StringArrToDoubleArr(param.getValueArray());
				this.oppositeDirection = MathCalcsLib.oppositeVector(m_direction);
				break;
				
			default:
				super.setParameter(param);
				return false;
		}
		return true;
	}

	public double[] getAmountOfLight(double[] point) {
		return m_color; // constant light, regardless of distance to target
	}

	public double[] getVectorToLight(double[] pointOfIntersection) {
		return oppositeDirection;		
	}	
	
	public double[] getDirection() {
		return m_direction;
	}
	
	public void setDirection(double []d) {
		this.m_direction = d;
		this.oppositeDirection = MathCalcsLib.oppositeVector(m_direction);
	}

}
