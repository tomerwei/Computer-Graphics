
public class RayIntersection {
	private SceneGeometricPrimitive primitive;
	private double distance;
	
	public RayIntersection(double distance, SceneGeometricPrimitive primitive) {
		this.primitive = primitive;
		this.distance = distance;
	}

	public SceneGeometricPrimitive getPrimitive() {
		return primitive;
	}

	public void setPrimitive(SceneGeometricPrimitive primitive) {
		this.primitive = primitive;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}	
	
}
