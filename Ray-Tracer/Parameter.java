
public class Parameter 
{	
	String m_name;
	String[] m_valueArray;
	
	public Parameter(String name, Double[] valueArray)
	{
		setValue(name, valueArray);
	}
	
	public Parameter(String name, String[] valueArray)
	{
		setValue(name, valueArray);
	}

	public void setValue(String name, String[] valueArray)
	{
		m_name = new String(name);
		m_valueArray = new String[valueArray.length];
		for (int i=0; i<valueArray.length; i++) {
			m_valueArray[i] = valueArray[i];
		}
	}
	
	public void setValue(String name, Double[] valueArray)
	{
		m_name = new String(name);
		m_valueArray = new String[valueArray.length];
		for (int i=0; i<valueArray.length; i++) {
			m_valueArray[i] = Double.toString(valueArray[i]);
		}
	}
    
    public String[] getValueArray()
    {
    	return m_valueArray;
    }
    
    public String getValueString()
    {
    	return m_valueArray[0];
    }
    
    public String getName()
    {
    	return m_name;
    }
	
}
