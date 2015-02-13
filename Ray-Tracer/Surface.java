import java.util.HashMap;
import java.util.Map;

public class Surface 
{
	enum tParts { MTL_TYPE, MTL_DIFFUSE, MTL_SPECULAR, MTL_AMBIENT, MTL_EMISSION, MTL_SHINESS, 
				  CHECKERS_SIZE, CHECKERS_DIFFUSE1, CHECKERS_DIFFUSE2, UNKNOWN };
	enum tTypes { TYPE_FLAT, TYPE_CHECKERS, TYPE_TEXTURE, TYPE_UNKNOWN };
	Map<String, tParts> stringToPart = null;
	Map<String, tTypes> stringToType = null;
	
	String m_typeStr = "";
	tTypes m_type = tTypes.TYPE_UNKNOWN;	
	double[] m_diffuse = { 0.8F, 0.8F, 0.8F };
	double[] m_specular = { 1.0F, 1.0F, 1.0F };
	double[] m_ambient = { 0.1F, 0.1F, 0.1F };
	double[] m_emission = { 0, 0, 0 };
	double m_shininess = 100.0F;
	double m_checkersSize = 0.1F;
	double[] m_checkersDiffuse1 = { 1.0F, 1.0F, 1.0F };
	double[] m_checkersDiffuse2 = { 0.1F, 0.1F, 0.1F };	
	
	public void initObject() 
	{		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("mtl-type", tParts.MTL_TYPE);
		stringToPart.put("mtl-diffuse", tParts.MTL_DIFFUSE);
		stringToPart.put("mtl-specular", tParts.MTL_SPECULAR);
		stringToPart.put("mtl-ambient", tParts.MTL_AMBIENT);		
		stringToPart.put("mtl-emission", tParts.MTL_EMISSION);
		stringToPart.put("mtl-shininess", tParts.MTL_SHINESS);
		stringToPart.put("checkers-size", tParts.CHECKERS_SIZE);
		stringToPart.put("checkers-diffuse1", tParts.CHECKERS_DIFFUSE1);
		stringToPart.put("checkers-diffuse2", tParts.CHECKERS_DIFFUSE2);				
		
		stringToType = new HashMap<String, tTypes>();
		stringToType.put("type_flat", tTypes.TYPE_FLAT);
		stringToType.put("type_checkers", tTypes.TYPE_CHECKERS);
	}
	
	public boolean setParameter(Parameter param) throws Exception
	{				
		boolean bWasProcessed = true;
		if (false == stringToPart.containsKey(param.getName())) {
			return false;
		}
		
		tParts part = stringToPart.get(param.getName());	
		switch (part)
		{
			
			case MTL_TYPE:
				m_typeStr = param.getValueString();
				m_type = stringToType.get(m_typeStr);				
				break;
			case MTL_DIFFUSE:
				m_diffuse = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case MTL_SPECULAR:
				m_specular = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case MTL_AMBIENT:
				m_ambient = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case MTL_EMISSION:
				m_emission = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case MTL_SHINESS:
				m_shininess = Double.parseDouble(param.getValueString());
				break;
			case CHECKERS_SIZE:
				m_checkersSize = Double.parseDouble(param.getValueString());
				break;
			case CHECKERS_DIFFUSE1:
				m_checkersDiffuse1 = SceneParser.StringArrToDoubleArr(param.getValueArray()); 
				break;
			case CHECKERS_DIFFUSE2:
				m_checkersDiffuse2 = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			
			default:
				bWasProcessed = false;				
		}		
		return bWasProcessed;
	}	
	
	public String getType() {
		return m_typeStr;
	}
	public void setType(String type) {
		this.m_typeStr = type;
		this.m_type = stringToType.get(type);
	}
	public double[] getDiffuse() {
		return m_diffuse;
	}
	public void setDiffuse(double[] diffuse) {
		this.m_diffuse = diffuse;
	}
	public double[] getSpecular() {
		return m_specular;
	}
	public void setSpecular(double[] specular) {
		this.m_specular = specular;
	}
	public double[] getAmbient() {
		return m_ambient;
	}
	public void setAmbient(double[] ambient) {
		this.m_ambient = ambient;
	}
	public double[] getEmission() {
		return m_emission;
	}
	public void setEmission(double[] emission) {
		this.m_emission = emission;
	}
	public double getShininess() {
		return m_shininess;
	}
	public void setShininess(double shininess) {
		this.m_shininess = shininess;
	}
	public double getCheckersSize() {
		return m_checkersSize;
	}
	public void setCheckersSize(double checkersSize) {
		this.m_checkersSize = checkersSize;
	}
	public double[] getCheckersDiffuse1() {
		return m_checkersDiffuse1;
	}
	public void setCheckersDiffuse1(double[] checkersDiffuse1) {
		this.m_checkersDiffuse1 = checkersDiffuse1;
	}
	public double[] getCheckersDiffuse2() {
		return m_checkersDiffuse2;
	}
	public void setCheckersDiffuse2(double[] checkersDiffuse2) {
		this.m_checkersDiffuse2 = checkersDiffuse2;
	}
	public tTypes getTypeId() {
		return m_type;
	}

	public void setTypeId(tTypes typeId) {
		this.m_type = typeId;
	}	
		 	
	// Returns the checkers color for a given 2D point in [0, 1] coordinates
	public double[] getCheckersColor(double[] point2D) 
	{
		 double checkersX = Math.abs(Math.floor(point2D[0] / m_checkersSize) % 2);
		 double checkersY = Math.abs(Math.floor(point2D[1] / m_checkersSize) % 2);
		 
		 if (checkersX == 0 && checkersY == 0) return m_checkersDiffuse2;
		 if (checkersX == 0 && checkersY == 1) return m_checkersDiffuse1;
		 if (checkersX == 1 && checkersY == 0) return m_checkersDiffuse1;
		 if (checkersX == 1 && checkersY == 1) return m_checkersDiffuse2;
		 
		 return null;
	}
}
