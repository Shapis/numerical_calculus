import java.util.ArrayList;

public class PontosInfo {


	private ArrayList<Double> coeficientesNewton = new ArrayList<Double>();
	private ArrayList<Double> pontosX = new ArrayList<Double>();
	private ArrayList<Double> pontosY = new ArrayList<Double>();

	public ArrayList<Double> getX() {
		return pontosX;
	}

	public void setX(ArrayList<Double> temp) {
		this.pontosX = temp;
	}
	
	public ArrayList<Double> getY() {
		return pontosY;
	}

	public void setY(ArrayList<Double> temp) {
		this.pontosY = temp;
	}

	public ArrayList<Double> getCoeficientesNewton() {
		return coeficientesNewton;
	}

	public void setCoeficientesNewton(ArrayList<Double> coeficientesNewton) {
		this.coeficientesNewton = coeficientesNewton;
	}
	
}
