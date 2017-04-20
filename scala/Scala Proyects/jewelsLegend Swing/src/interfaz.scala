
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension
import javax.swing._
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder
import java.awt.Image;
import java.awt.Color;

object interfaz extends App {
	
	//                 0    1    2    3    4    5    6    7    8
	def letras = List(' ', 'A', 'R', 'N', 'V', 'P', 'M', 'G', 'B')
	
	
  	/**
  	 * Funci�n que recibe un JFrame, la dificultad, un numero entre 1 y 3 , 
	 * genera un tablero con las caracter�sticas del nivel indicado y ejecuta 
	 * el bucle principal del juego
  	 * 
  	 */
	def iniciarPartida(ventana:JFrame, dificultad:Int, modo:Char) = {
		val partida = jewelsLegend.getLevel(dificultad)
		val filas = partida._1
		val columnas = partida._2
		val puntuacion = 0
		val tablero = jewelsLegend.generarTablero(0, filas, columnas, dificultad)
		ventana.dispose()
		bucle(
				tablero,dificultad,filas,columnas,puntuacion,modo)

	}
	  
	/**
	 * Funci�n recursiva que ejecuta las instrucciones principales del juego 
	 * hasta que vidas == 0, que informa al jugador que ha perdido todas las vidas.
	 * Si se consiguen eliminar todas las celdas de un tablero sin perder se
	 * genera un tablero nuevo para continuar jugando con las vidas que se 
	 * ten�an antes. Se reinicia la puntuaci�n para el nuevo tablero y se guarda
	 * una puntuaci�n total acumulada con la suma de las puntuaciones de cada tablero 
	 * 
	 */
	def bucle(
			filas:Int, 
			columnas:Int, 
			tablero:List[Int],
			vidas:Int, 
			puntuacion:Int, 
			posMax:Int, 
			puntuacionTotal:Int, 
			dificultad:Int, 
			partidas:Int,
			modo:Char):Unit = {
		//Si el numero de vidas es 0 se acaba el juego
		if(vidas == 0){
			JOptionPane.showMessageDialog(
					null,
					"HAS PERDIDO TODAS LAS VIDAS, JUEGO TEMINADO!!",
					null, 
					JOptionPane.ERROR_MESSAGE)
		}
		else{
			//Si el jugador termina un tablero sin perder las vidas...
			if(antique.haGanado(filas, columnas, tablero)) {
				
				JOptionPane.showMessageDialog(
						null, 
							"���ENHORABUENA, HAS SUPERADO EL TABLERO!!!")
				
				//Se genera un nuevo tablero manteniendo vidas, y acumulando a un 
				//total los puntos conseguidos en el tablero anterior
				//Para el nuevo tablero se inicializa la puntuaci�n
				val partida = antique.dameDatosPartida(dificultad)
				val filas = partida._1
				val columnas = partida._2
				val bloques = partida._3
				val bombas = partida._4
				val tablero = antique.generaTablero(filas*columnas,bloques, Nil)
				val posicionesBombas = antique.generarBombas(bombas, tablero.length, Nil)
				val tableroInicial = antique.introducirBombas(posicionesBombas, tablero)
				
				bucle(
						filas,
						columnas,
						tableroInicial , 
						vidas, 
						0, 				//Puntuaci�n para el nuevo tablero
						0,  
						puntuacion + puntuacionTotal, //Puntuaci�n total
						dificultad, 
						partidas+1, 	//Se incrementa el numero de partidas
						modo
					)
				
			}
			else{
				val puntuacionMaxima = antique.mejorJugada(
													tablero,
													0, 
													filas, 
													columnas, 
													Nil, 
													0, 
													0, 
													0, 
													0)
													
				val mejorPuntuacion = puntuacionMaxima._1
				val puntuacionMaximaFila = puntuacionMaxima._2
				val puntuacionMaximaColumna = puntuacionMaxima._3            
				
				mostrarTablero(
						filas, 
						columnas, 
						tablero, 
						vidas, 
						puntuacion,
						puntuacionMaximaFila*columnas+puntuacionMaximaColumna, 
						mejorPuntuacion, 
						puntuacionTotal, 
						dificultad, 
						partidas, 
						modo)
			}
			
		}
		
		
	}	 
		
	/**
	 * Funci�n que genera los elementos para mostrar el tablero en un JFrame
	 */
	def mostrarTablero(
			filas:Int, 
			columnas:Int, 
			tablero:List[Int],
			vidas:Int,
			puntuacion:Int, 
			posMax:Int, 
			mejorPuntuacion:Int, 
			puntuacionTotal: Int, 
			dificultad:Int, 
			partidas:Int, 
			modo:Char) = {
		
		
		val tableroGrafico = new JFrame("ANTIQUE BLOCKS")
		tableroGrafico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tableroGrafico.setBounds(200, 0, 800,720);
		tableroGrafico.getContentPane().setLayout(null);
		tableroGrafico.getContentPane().setLayout(new BorderLayout(0, 0));
		
		val panel = new JPanel();
		panel.setBounds(86, 150, 380, 205);
		tableroGrafico.getContentPane().add(panel);
		panel.setLayout(null)
		
		val panel_tablero = new JPanel();
		panel_tablero.setBounds(84, 150, 600, 500);
		panel.add(panel_tablero);
		panel_tablero.setLayout(new GridLayout(filas, columnas, 0, 0));
           
		
		//Se introducen los botones en el panel_tablero, y la informaci�n de la partida
  		anadirBotones(
				tableroGrafico,
				panel_tablero, 
				filas, 
				columnas, 
				tablero, 
				tablero, 
				vidas, 
				puntuacion, 
				posMax, 
				puntuacionTotal, 
				dificultad, 
				partidas, 
				modo)
  		
  		val vidas_label = new JLabel("Vidas");
  		vidas_label.setBounds(100, 35, 70, 30);
  		vidas_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(vidas_label);
  		
  		val numero_vidas_label = new JLabel(""+vidas);
  		numero_vidas_label.setBounds(290, 35, 70, 30);
  		numero_vidas_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_vidas_label);
  		
  		val partida_label = new JLabel("Partida");
  		partida_label.setBounds(100, 70, 150, 30);
  		partida_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(partida_label);
  		
  		val numero_partida_label = new JLabel(""+partidas);
  		numero_partida_label.setBounds(290, 70, 70, 30);
  		numero_partida_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_partida_label);
  		
  		val modo_label_texto = new JLabel("Modo");
  		modo_label_texto.setBounds(100, 105, 150, 30);
  		modo_label_texto.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(modo_label_texto);
      		
      		
  		val modo_label = new JLabel();
  		if(modo == 'm'){
			modo_label.setText("M")
  		}else{
  			modo_label.setText("A")
  		}
		modo_label.setBounds(290, 105, 70, 30);
		modo_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
		panel.add(modo_label);
      		
      		
  		val numero_puntuacion_total_label = new JLabel("Puntuaci�n total");
  		numero_puntuacion_total_label.setBounds(350, 35, 250, 30);
  		numero_puntuacion_total_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_total_label);
  		
  		val numero_puntuacion_total = new JLabel(""+puntuacionTotal);
  		numero_puntuacion_total.setBounds(620, 35, 70, 30);
  		numero_puntuacion_total.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_total);
  		
  		val puntuacion_label = new JLabel("Puntuaci�n");
  		puntuacion_label.setBounds(350, 70, 150, 30);
  		puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(puntuacion_label);
  		
  		val numero_puntuacion_label = new JLabel(""+puntuacion);
  		numero_puntuacion_label.setBounds(620, 70, 70, 30);
  		numero_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_label);      		
  		
  		val maxima_puntuacion_label = new JLabel("Mejor puntuaci�n");
  		maxima_puntuacion_label.setBounds(350, 105, 250, 30);
  		maxima_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(maxima_puntuacion_label);
  		
  		val maxima_puntuacion = new JLabel(""+mejorPuntuacion);
  		maxima_puntuacion.setBounds(620, 105, 70, 30);
  		maxima_puntuacion.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(maxima_puntuacion);
      	
  		
  		//Si el modo elegido es autom�tico se crea el bot�n que permite hacer 
  		//continuar la partida de manera autom�tica
  		if(modo =='a'){
			val botonContinuar = new JButton("Continuar")
      		botonContinuar.setBounds(350, 660, 100, 23)
      		botonContinuar.addActionListener(new java.awt.event.ActionListener() {
                    
				def actionPerformed(evento:ActionEvent):Unit = {
					botonActionPerformed(
							tableroGrafico, 
							tablero,
							filas, 
							columnas,  
							posMax, 
							vidas, 
							puntuacion, 
							posMax, 
							puntuacionTotal, 
							dificultad, 
							partidas, 
							modo)
	                    
				}
   
			}
			);
			panel.add(botonContinuar)
		}
		tableroGrafico.setVisible(true)
		tableroGrafico.setResizable(false)
		
	}
	/**
	 * Secuencia de instrucciones al pulsar un bloque con el rat�n
	 */
	def  botonActionPerformed(
			ventana:JFrame, 
			tablero:List[Int],
			filas:Int, 
			columnas:Int , 
			pos:Int, 
			vidas:Int, 
			puntuacion:Int, 
			posMax:Int, 
			puntuacionTotal: Int, 
			dificultad:Int , 
			partidas:Int, 
			modo:Char): Unit = { 
		
		
		//si no es una posici�n vac�a
		if(antique.obtenerEntero(pos, tablero)!=0) {
			//Se extrae la fila y la columna
 			val columna = pos % columnas
 			val fila = (pos - columna)/columnas
 			
 			//si es bomba suena una explosi�n
 			if(antique.esBomba(fila*columnas+columna, tablero)){
   				antique.reproduceSonidoExplosion()
 			}
 			/*  Se elimina la celda de la posici�n recibida 
				Se obtiene el tablero, las vidas y los puntos como consecuencia 
				de la eliminaci�n */
	 		val dato = antique.obtenerEntero(fila*columnas+columna,tablero)
	 		val listaEliminar = antique.consultaTablero(
											dato,
											fila, 
											columna,
											filas, 
											columnas, 
											tablero)
											
	 		val resultado = antique.eliminaCeldas(
											listaEliminar,
											listaEliminar.length,pos, 
											tablero, 
											vidas, 
											puntuacion)
			val tablero_1 = resultado._1
			val vidas_1= resultado._2
			val puntuacionFinal = resultado._3
			//Se reestructura el tablero
			val tablero2 = antique.subirCeros(tablero_1, 0, 0, filas, columnas, false)
			val tablero_final = antique.desplazarCerosDerecha(
											tablero2, 
											filas-1, 
											columnas-1, 
											filas, 
											columnas, 
											false)
			//se cierra la ventana 
			ventana.dispose()
			//Y se vuelve a ejecutar el bucle del juego
			bucle(
					filas, 
					columnas, 
					tablero_final, 
					vidas_1,
					puntuacionFinal, 
					posMax, 
					puntuacionTotal, 
					dificultad, 
					partidas,
					modo)
		}else{
			Nil
		}

	}
	
	
	/**
	 * Funci�n que a�ade los botones a un JPanel recursivamente.
	 * A cada bot�n se le a�ade una imagen correspondiente al numero que contiene 
	 * en el tablero, y se le a�ade tambi�n el m�todo que ejecutar� al pulsarlo si el modo 
	 * es manual. En caso de ser autom�tico los botones no tienen accion a ejecutar.
	 */
	def anadirBotones(
			v:JFrame, 
			panel:JPanel, 
			filas:Int, 
			columnas:Int, 
			tablero:List[Int],
			tablero_1:List[Int], 
			vidas:Int, 
			puntuacion:Int, 
			posMax:Int, 
			puntuacionTotal: Int, 
			dificultad:Int, 
			partidas:Int, 
			modo:Char ): Unit ={
			
		if(tablero.length == 0 ){
			Nil
		}else{
			
			val boton = new JButton();
			val dato = antique.obtenerEntero(filas*columnas - tablero.length, tablero_1)
	        val botonColoreado = cambiarColorBoton(boton, dato, filas, columnas)
			if(filas*columnas - tablero.length == posMax)
				botonColoreado.setBorder(new LineBorder(Color.yellow,4))
				
			panel.add(botonColoreado);
			if(modo == 'm'){
				botonColoreado.addActionListener(new java.awt.event.ActionListener() {
				def actionPerformed(evento:ActionEvent):Unit = {    
					botonActionPerformed(
							v, 
							tablero_1,
							filas, 
							columnas, 
							filas*columnas - tablero.length, 
							vidas, 
							puntuacion,
							posMax, 
							puntuacionTotal, 
							dificultad, 
							partidas,
							modo);
				}});
			}
			anadirBotones(
					v, 
					panel, 
					filas, 
					columnas, 
					tablero.tail, 
					tablero_1,vidas, 
					puntuacion,
					posMax, 
					puntuacionTotal, 
					dificultad, 
					partidas, 
					modo)
		}
	}
	
	
	/**
	 * Funci�n que recibe un numero y un bot�n y le coloca una imagen distinta
	 * dependiendo del numero recibido
	 */
	def cambiarColorBoton(
			boton: JButton, 
			numero: Int,
			filas:Int, 
			columnas:Int): JButton = {
		    
		
		val iconoBomba = new ImageIcon(
				(new ImageIcon("res/bomba.gif")).getImage().getScaledInstance(  
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueAzul = new ImageIcon(
				(new ImageIcon("res/azul.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueAmarillo = new ImageIcon(
				(new ImageIcon("res/amarillo.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueBlanco = new ImageIcon(
				(new ImageIcon("res/blanco.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas,
															Image.SCALE_FAST))
		
		val iconoBloqueRojo= new ImageIcon(
				(new ImageIcon("res/rojo.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueVerde = new ImageIcon(
				(new ImageIcon("res/verde_claro.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueMorado = new ImageIcon(
				(new ImageIcon("res/morado.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueMarron = new ImageIcon(
				(new ImageIcon("res/marron.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		
		if(numero == 0)
			boton.setBackground(Color.WHITE)
		else if(numero == 1)
			boton.setIcon(iconoBloqueAzul)
		else if(numero == 2)
		    boton.setIcon(iconoBloqueRojo)
		else if(numero == 3)
			boton.setIcon(iconoBloqueMorado)
		else if(numero == 4)
			boton.setIcon(iconoBloqueVerde)
		else if(numero == 5)
			boton.setIcon(iconoBloqueBlanco)
		else if(numero == 6)
			boton.setIcon(iconoBloqueMarron)
		else if(numero == 7)
			boton.setIcon(iconoBloqueAmarillo)
		else
			boton.setIcon(iconoBomba)
	   
		boton
	}	
			    
}