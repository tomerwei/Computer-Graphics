

public abstract class SceneGeometricPrimitive implements SceneObjectsInterface{
	
	Surface m_surface = null;
	
	abstract public double FindIntersection(Ray ray);	
	
	abstract public double[] GetTextureCoords(double[] point);
	
	abstract public double[] GetNormal(double[] point);
	
	public void Finalize()  {		
	}

	public void initObject() 
	{
		m_surface = new Surface();
		m_surface.initObject();
	}
	
	public Surface getSurface() 
	{
		return m_surface;
	}
		
	// Return the color at the given point (could be flat, texture, checkers)
	public double[] getColorAt(double[] point) 
	{
		String type = m_surface.getType();
		if(type.equalsIgnoreCase("checkers")) {
			return m_surface.getCheckersColor(GetTextureCoords(point));			
		}	
		else return m_surface.getDiffuse();
	}

	public boolean setParameter(Parameter param) throws Exception {		
		return (m_surface.setParameter(param));
	}	

}
