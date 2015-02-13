public class LightPoint extends Light {
	
	/*default values*/
	double[] m_attenuation; 
	
	public void initObject() 
	{
		super.initObject();
		this.m_pos = new double[]{0, 0 ,0};
		stringToPart.put("pos", Light.tParts.POS);
		this.m_attenuation = new double[]{1, 0 ,0};
		stringToPart.put("attenuation", Light.tParts.ATTENUATION);
	}
	
	public boolean setParameter(Parameter param) throws Exception 
	{
		if (super.setParameter(param)) return true;
		
		tParts part = stringToPart.get(param.getName());
		
		switch (part)
		{
			case POS:
				m_pos = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
				
			default:
				super.setParameter(param);
				return false;
		}				
		return true;
	}

	public double[] getAmountOfLight(double[] point) {
		double d = MathCalcsLib.norm(MathCalcsLib.calcPointsDiff(getPosition(), point));				
		double totalAttenuation = 1 / (getAttenuation()[2] * d * d + getAttenuation()[1] * d + getAttenuation()[0]);		
		double[] result = { getColor()[0] * totalAttenuation, getColor()[1] * totalAttenuation, getColor()[2]*totalAttenuation};  		
		return result;
	}
	
	public double[] getVectorToLight(double[] pointOfIntersection) {
		double[] vec = MathCalcsLib.calcPointsDiff(pointOfIntersection, getPosition());
		MathCalcsLib.normalize(vec);		
		return vec;
	}
	
	public double[] getAttenuation() {
		return m_attenuation;
	}
	
	public void setAttenuation(double[] a) {
		this.m_attenuation = a;
	}

}
