import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		
		Scanner texto = new Scanner(System.in);
		String localArquivoEntrada;
		String localArquivoSaida;
		System.out.println("Qual o local do arquivo de entrada no seu HD? (Ex: C:\\\\MinhaEntrada.txt)");
		localArquivoEntrada = texto.next();
		System.out.println("Onde quer salvar o arquivo de saida no seu HD? (Ex: C:\\\\MinhaSaida.txt)");
		localArquivoSaida = texto.next();
		
		
		
		ArrayList<PontosInfo> pontosInfoArray = lerArquivo(localArquivoEntrada);
		ArrayList<PontosInfo> infoResultados = executarAlgoritmo(pontosInfoArray);
		salvarArquivo(infoResultados, localArquivoSaida);
		
		System.out.println("Digite qualquer coisa para encerrar.");
		localArquivoSaida = texto.next();
		texto.close();

	}

	private static ArrayList<PontosInfo> lerArquivo(String localArquivoEntrada) {
		BufferedReader br = null;
		ArrayList<PontosInfo> returnArray = new ArrayList<PontosInfo>();
		
		
		try {

			String currentLine;

			br = new BufferedReader(new FileReader(localArquivoEntrada));

			while ((currentLine = br.readLine()) != null) {

				ArrayList<Double> myDoubles = new ArrayList<Double>();
				ArrayList<Double> tempList1 = new ArrayList<Double>();
				ArrayList<Double> tempList2 = new ArrayList<Double>();
				PontosInfo temp = new PontosInfo();

				Matcher matcher = Pattern.compile(
						"[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?").matcher(
						currentLine);

				while (matcher.find()) {
					double k = Double.parseDouble(matcher.group());
					myDoubles.add(k);
				}
				myDoubles.remove(0);

				for (int k = 1; k < myDoubles.size(); k = k + 2) {
					tempList1.add(myDoubles.get(k - 1));
				}
				temp.setX(tempList1);

				for (int k = 2; k < myDoubles.size() + 1; k = k + 2) {
					tempList2.add(myDoubles.get(k - 1));
				}
				temp.setY(tempList2);

				returnArray.add(temp);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Arquivo não encontrado!");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("Arquivo não encontrado!");
			}
		}

		return returnArray;
	}

	private static ArrayList<PontosInfo> executarAlgoritmo(
			ArrayList<PontosInfo> pontosInfoArray) {

		ArrayList<PontosInfo> resultados = new ArrayList<PontosInfo>();

		ArrayList<Double> coeficientesDiferencasDivididas = new ArrayList<Double>();
		coeficientesDiferencasDivididas = interpolar(pontosInfoArray.get(0));

		
		
		resultados = integrar(pontosInfoArray.get(0).getX(), pontosInfoArray.get(1).getX(), pontosInfoArray.get(1).getY(), coeficientesDiferencasDivididas);
		
		
		

		return resultados;
	}
	
	private static ArrayList<PontosInfo> integrar(ArrayList<Double> pontosX, ArrayList<Double> intervalosIntegracaoInicial, ArrayList<Double> intervalosIntegracaoFinal , ArrayList<Double> coeficientesDiferencasDivididas) {
		ArrayList<PontosInfo> retorno = new ArrayList<PontosInfo>();
		int precisao = 10;
		
		for (int k = 0 ; k < intervalosIntegracaoFinal.size() ; k++) {
			ArrayList<Double> temp1 = new ArrayList<Double>();
			ArrayList<Double> temp2 = new ArrayList<Double>();
			PontosInfo retornoAdd = new PontosInfo();
			retornoAdd.setCoeficientesNewton(coeficientesDiferencasDivididas);
			
			for (int i = 1 ; i <= 100 ; i = i*10) {
			temp1.add(integralTrapezios(pontosX, coeficientesDiferencasDivididas, precisao*(i), intervalosIntegracaoInicial.get(k), intervalosIntegracaoFinal.get(k)));
			temp2.add(integralSimpson(pontosX, coeficientesDiferencasDivididas, precisao*(i), intervalosIntegracaoInicial.get(k), intervalosIntegracaoFinal.get(k)));
			}
			retornoAdd.setX(temp1);
			retornoAdd.setY(temp2);
			retorno.add(retornoAdd);
			
		}
		
		return retorno;
	}
	

	private static ArrayList<Double> interpolar(PontosInfo pontos) {
		ArrayList<Double> coeficientes = new ArrayList<Double>();
		ArrayList<Double> tempY = new ArrayList<Double>();
		ArrayList<Double> tempX = new ArrayList<Double>();
		ArrayList<Double> temp = new ArrayList<Double>();

		tempY.addAll(pontos.getY());
		tempX.addAll(pontos.getX());

		coeficientes.add(tempY.get(0));

		for (int k = 0; k < tempX.size() - 1; k++) {
			temp.clear();

			for (int i = 0; (i + k) + 2 <= tempX.size(); i++) {

				temp.add(diferencaDividida(tempY.get(i), tempY.get(i + 1),
						tempX.get(i), tempX.get(i + k + 1)));

			}

			coeficientes.add(temp.get(0));
			tempY.clear();
			tempY.addAll(temp);
		}

		return coeficientes;
	}

	private static Double diferencaDividida(double y1, double y2, double x1,
			double x2) {

		return ((y2 - y1) / (x2 - x1));
	}

	private static Double resultadoFuncaoNoPonto(ArrayList<Double> pontosX,
			ArrayList<Double> diferencasDivididas, Double meuPonto) {
		double resultado = diferencasDivididas.get(0);
		double temp = 1;

		for (int k = 1; k < diferencasDivididas.size(); k++) {

			temp = temp * (meuPonto - pontosX.get(k - 1));

			resultado = resultado + temp * diferencasDivididas.get(k);
		}

		return resultado;
	}
	
	private static Double integralTrapezios(ArrayList<Double> pontosX, ArrayList<Double> diferencasDivididas, int numeroDeIntervalos, double intervaloInferior, double intervaloSuperior) {
		
		ArrayList<Double> pontosDoIntervaloDeIntegracao = new ArrayList<Double>();
		ArrayList<Double> resultadosDaFuncaoNosPontos = new ArrayList<Double>();
		double temp = Math.abs(intervaloSuperior-intervaloInferior)/numeroDeIntervalos;
		double temp2 = intervaloInferior;
		double resultado = 0;
		
		
		
		pontosDoIntervaloDeIntegracao.add(intervaloInferior);
		
		for (int k = 0 ; k < numeroDeIntervalos ; k++) {
			temp2 = temp2 + temp;
		pontosDoIntervaloDeIntegracao.add(temp2);
		
		}
		
		for (int k = 0 ; k < pontosDoIntervaloDeIntegracao.size() ; k++) {
			resultadosDaFuncaoNosPontos.add(resultadoFuncaoNoPonto(pontosX, diferencasDivididas, pontosDoIntervaloDeIntegracao.get(k)));
		}
		
		
		
		resultado = 0;
		
		for (int k = 0 ; k < resultadosDaFuncaoNosPontos.size()-1 ; k++) {
			resultado = resultado + ((resultadosDaFuncaoNosPontos.get(k)+resultadosDaFuncaoNosPontos.get(k+1))/2)*temp;
		}
		return resultado;
	}
	
	private static Double integralSimpson(ArrayList<Double> pontosX, ArrayList<Double> diferencasDivididas, int numeroDeIntervalos, double intervaloInferior, double intervaloSuperior) {
		double pares = 0;
		double impares = 0;
		double extremos = 0;
		double resultado = 0;
		double temp = Math.abs(intervaloSuperior-intervaloInferior)/numeroDeIntervalos;
		double temp2 = intervaloInferior;
		ArrayList<Double> pontosDoIntervaloDeIntegracao = new ArrayList<Double>();
		ArrayList<Double> resultadosDaFuncaoNosPontos = new ArrayList<Double>();
		
pontosDoIntervaloDeIntegracao.add(intervaloInferior);
		
		for (int k = 0 ; k < numeroDeIntervalos ; k++) {
			temp2 = temp2 + temp;
		pontosDoIntervaloDeIntegracao.add(temp2);
		
		}
		
		for (int k = 0 ; k < pontosDoIntervaloDeIntegracao.size() ; k++) {
			resultadosDaFuncaoNosPontos.add(resultadoFuncaoNoPonto(pontosX, diferencasDivididas, pontosDoIntervaloDeIntegracao.get(k)));
		}
		
		
		for (int k = 1 ; k < resultadosDaFuncaoNosPontos.size() ; k = k +2) {
		pares = pares + resultadosDaFuncaoNosPontos.get(k);
		}
		
		for (int k = 2 ; k < resultadosDaFuncaoNosPontos.size() ; k = k +2) {
			impares = impares + resultadosDaFuncaoNosPontos.get(k);
			}
		
		extremos = resultadosDaFuncaoNosPontos.get(0) + resultadosDaFuncaoNosPontos.get(resultadosDaFuncaoNosPontos.size()-1);
		
		resultado = (temp/3)*(extremos+(4*pares)+(2*impares));
		
		
		
		
		return resultado;
	}

	private static int salvarArquivo(ArrayList<PontosInfo> resultados, String localArquivoSaida) {

		String stringTemp = "Inicialização de variável.";
		String stringFinal = "Inicialização de variável.";
		StringBuilder stringBuilder = new StringBuilder();
		
		
		System.out.println("");
		for (Double i: resultados.get(0).getCoeficientesNewton()){
		stringTemp = i.toString() + " ";
		stringBuilder.append(stringTemp);
		}
		stringBuilder.append(System.getProperty("line.separator"));
		
		
		for (int i = 0 ; i < resultados.size(); i++) {
			
			for (int j = 0 ; j < resultados.get(0).getX().size() ; j++){
			stringTemp = resultados.get(i).getX().get(j).toString() + " ";
			stringBuilder.append(stringTemp);
			}
			for (int j = 0 ; j < resultados.get(0).getY().size() ; j++){
				stringTemp = resultados.get(i).getY().get(j).toString() + " ";
				stringBuilder.append(stringTemp);
				}
			
			
			stringBuilder.append(System.getProperty("line.separator"));
		}
		
		
		stringFinal = stringBuilder.toString();
		System.out.println(stringFinal);
		
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					localArquivoSaida));

			writer.write(stringFinal);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("O arquivo não foi salvo!");
		}
		
		return 0;

	}

}
