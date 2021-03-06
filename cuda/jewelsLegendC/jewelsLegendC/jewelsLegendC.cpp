// jewelsLegendC.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Diamante.h"
#include <string.h>
#include <time.h>
#include <string>
#include <stdlib.h>
#include <fstream>
#include <iostream>
# include <stdio.h>
# include <conio.h>


using namespace std;
int nColores = 8;
enum posicion { todos, arriba, abajo, derecha, izquierda };
FILE *doc;
FILE *leer;

void printDiamante(Diamante *tablero, HANDLE hConsole, int filas, int columnas);
int comprobarIgualesPos(Diamante *tablero, int posX, int posY, posicion pos, int filas, int columnas);
int comprobarIgualesDer(Diamante *tablero, int posX, int posY, int filas, int columnas);
int comprobarIgualesAbajo(Diamante *tablero, int posX, int posY, int filas);
int comprobarIgualesIzquierda(Diamante *tablero, int posX, int posY, int filas);
int comprobarIgualesArriba(Diamante *tablero, int posX, int posY, int filas);
Diamante *explotarIguales(Diamante *tablero, int posX, int posY, posicion pos, int filas, int columnas);
Diamante *moverAbajo(Diamante *tablero, int filas, int columnas);
Diamante *menuBomba(Diamante *tablero, int tipBomba, int filas, int columnas);
bool explotan(Diamante *tablero, int f1, int c1, int f2, int c2, bool ady, int filas, int columnas);
void guardarPartida(Diamante *tablero, int filas, int columnas, string dificultad);

bool hasMoreMovements(Diamante *tablero, int filas, int columnas) {
	bool expl = false;
	int posX = 0;
	while (posX < filas && !expl) {
		for (int posY = 0; posY < columnas; posY++) {
			if (posX + 1 < filas && explotan(tablero, posX, posY, posX + 1, posY,true, filas, columnas)) { // Abajo
				expl = true;
			}
			else if (posY + 1 < columnas && explotan(tablero, posX, posY, posX, posY + 1, true, filas, columnas)) { //Derecha
				expl = true;
			}
			else if (posY - 1 >= 0 && explotan(tablero, posX, posY, posX, posY - 1, true, filas, columnas) ) { //Izquierda
				expl = true;
			}
			else if (posX - 1 >= 0 && explotan(tablero, posX, posY, posX-1, posY, true, filas, columnas)) {//Arriba
				expl = true;
			}
		}
		posX++;
	}

	return expl;
}

Diamante *comprobarCadena(Diamante *tablero, int posX, int posY, int filas, int columnas) {
	int comFilas = comprobarIgualesDer(tablero, posX, posY, filas,columnas) + comprobarIgualesIzquierda(tablero, posX, posY, filas);
	int comColumnas = comprobarIgualesAbajo(tablero, posX, posY,filas) + comprobarIgualesArriba(tablero, posX, posY, filas);
	//Comprobamos cual es la cadena mas larga para pasar a explotar la posicion del primer elemento;
	if (comFilas > comColumnas) {
		int i = posY;
		while (tablero[posX*filas + posY].color == tablero[posX*filas + (i - 1)].color && i>0) {
			i--;
		}
		tablero = explotarIguales(tablero, posX, i,derecha, filas, columnas);
	}
	else {
		int i = posX;
		while (tablero[posX*filas + posY].color == tablero[(i - 1)*filas + posY].color && i>0) {
			i--;
		}
		tablero = explotarIguales(tablero, i, posY,abajo, filas, columnas);
	}
	
	return tablero;
}

Diamante *explotarIguales(Diamante *tablero, int posX, int posY, posicion pos, int filas, int columnas) {
	
		if (comprobarIgualesDer(tablero, posX, posY, filas, columnas) >= 1 && pos == derecha)
			tablero = explotarIguales(tablero, posX, posY + 1, derecha, filas, columnas);

		if (comprobarIgualesAbajo(tablero, posX, posY, filas) >= 1 && pos == abajo)
			tablero = explotarIguales(tablero, posX + 1, posY, abajo, filas, columnas);

	tablero[posX*filas + posY].color = 0;

	return tablero;
}
/*Diamante *comprobarIguales(Diamante *diam) {
	
	for (int i = 0; i < filas; i++) {  //Recorremos el array en busca de cadenas de tres numeros iguales
		for (int j = 0; j < columnas; j++) {
			if (diam[(i*filas) + j].color != 0) {
				int contFilas = comprobarIgualesDer(diam, i, j);
				int contColumnas = comprobarIgualesAbajo(diam, i, j);

				if (contFilas > 1) 
					diam = explotarIguales(diam, i, j);
				if (contColumnas > 1) 
					diam = explotarIguales(diam, i, j);
			}
		}	
	}
	diam = moverAbajo(diam);
	return diam;
}*/
int comprobarIgualesArriba(Diamante *tablero, int posX, int posY, int filas) {
	int cont = 0;
	if (posX - 1 >= 0 && tablero[(posX*filas) + posY].color == tablero[((posX-1) * filas) + posY].color) {
		cont = 1 + comprobarIgualesArriba(tablero, posX-1, posY, filas);
	}
	return cont;
}
int comprobarIgualesIzquierda(Diamante *tablero, int posX, int posY, int filas) {
	int cont = 0;
	if (posY - 1 >= 0 && tablero[(posX*filas) + posY].color == tablero[(posX * filas) + posY - 1].color ) {
		cont = 1 + comprobarIgualesIzquierda(tablero, posX, posY - 1, filas);
	}
	return cont;
}

int comprobarIgualesDer(Diamante *tablero, int posX, int posY, int filas, int columnas) {
	int cont = 0;
	if (posY + 1 < columnas && tablero[(posX*filas) + posY].color == tablero[(posX * filas) + posY + 1].color) {
		cont = 1 + comprobarIgualesDer(tablero, posX, posY + 1, filas, columnas);
	}
	return cont;
}
int comprobarIgualesAbajo(Diamante *tablero, int posX, int posY, int filas) {
	int cont = 0;
	//if(posX >= filas - 1 && posY >= columnas - 1)
	if (posX + 1 < filas && tablero[(posX*filas) + posY].color == tablero[((posX + 1) * filas) + posY].color) {
		cont = 1 + comprobarIgualesAbajo(tablero, posX + 1, posY , filas);
	}
	return cont;
}
int comprobarIgualesPos(Diamante *tablero, int posX, int posY,posicion pos, int filas, int columnas) {
	int cont = 0;
	switch (pos)
	{
	case todos:
		cont += comprobarIgualesPos(tablero, posX, posY, derecha, filas,columnas);
		cont += comprobarIgualesPos(tablero, posX, posY, izquierda, filas, columnas);
		cont += comprobarIgualesPos(tablero, posX, posY, arriba, filas, columnas);
		cont += comprobarIgualesPos(tablero, posX, posY, abajo, filas, columnas);
		break;
	case derecha:
		if (posY + 1 < columnas && tablero[(posX*filas) + posY].color == tablero[(posX * filas) + posY + 1 ].color) { // comprobamos derecha 
			cont += 1 + comprobarIgualesPos(tablero, posX, posY + 1,derecha, filas, columnas);
		}
		break;
	case izquierda:
		if (posY - 1 >= 0 && tablero[(posX*filas) + posY].color == tablero[(posX * filas) + posY - 1].color) { //comprobamos izquierda
			cont += 1 + comprobarIgualesPos(tablero, posX, posY - 1,izquierda, filas, columnas);
		}
		break;
	case abajo:
		if (posX + 1 < filas && tablero[(posX*filas) + posY].color == tablero[((posX + 1) * filas) + posY].color) { //comprobamos abajo
			cont += 1 + comprobarIgualesPos(tablero, posX + 1, posY,abajo, filas, columnas);
		}
	break;
	case arriba:
		if (posX - 1 >= 0 && tablero[(posX*filas) + posY].color == tablero[((posX - 1) * filas) + posY].color) { //comprobamos arriba
			cont += 1 + comprobarIgualesPos(tablero, posX - 1, posY,arriba, filas, columnas);
		}
	break;
	default:
		break;
	}

	return cont;

}

void inicicializarArray(Diamante *tablero, int filas, int columnas, int nColores){
	int numAleatorio = 0;

	cout << "\n\n";
	//srand(time(0));
	for (int i = 0; i < filas; i++) {  //llenamos el array de nums aleatorios
		for (int j = 0; j < columnas; j++) {
			
			numAleatorio = rand() % nColores + 1;
			tablero[(i*filas) + j] = Diamante(i, j); 
			tablero[(i*filas) + j].color = numAleatorio;

		}
	}
}

Diamante *rellenarCeros(Diamante *tablero, int nColores, int filas, int columnas) {
	int numAleatorio = 0;
	srand(time(NULL));//Cambiamos semillas

	for (int i = 0; i < filas; i++) {  //recorremos array
		for (int j = 0; j < columnas; j++) {

			if (tablero[(i*filas) + j].color == 0) {
				numAleatorio = rand() % nColores + 1;
				tablero[(i*filas) + j] = Diamante(i, j);
				tablero[(i*filas) + j].color = numAleatorio;
			}
			

		}
	}
	return tablero;
}

//funcion para mover los diamantes hacia abajo
//Recorremos la matriz de abajo hacia arriba para no tener que repetir movimientos
Diamante *moverAbajo(Diamante *tablero, int filas, int columnas) {
	int pos = 0;
	int FilaAux = 0;
	//Recorremos hacia atras el array
	for (int i = columnas - 1; i >= 0; i--) {
		for (int j = filas - 1; j >= 0; j--) {

			FilaAux = j;
			pos = (j*filas)+i;
			if (tablero[(j*filas) + i].color == 0) {
				//Buscamos la primera posicion de la columna con color !=0
				while (tablero[((FilaAux)*filas) + i].color == 0 && FilaAux > 0) {
					FilaAux -= 1;
				}

				//Intercambiamos colores
				int colorAux = tablero[(FilaAux*filas) + i].color;
				tablero[(FilaAux*filas) + i].color = 0;
				tablero[pos].color = colorAux;
				
								
			}
		}
	}
	return tablero;
}
////MUEVE CEROS HACIA IZQUIERDA
int moverIzquierda(Diamante *tablero, int filas, int columnas) {
	int columnaAux = 0;
	int columna1 = 0;
	
	for (int i = filas - 1; i >= 0; i--) {
		for (int j = columnas - 1; j >= 0; j--) {
			columnaAux = j;
			columna1 = j;
			if (tablero[(i*filas)+j].color == 0) {
				//Buscamos la siguiente columna !=0
				while (tablero[(i*filas) + columnaAux].color == 0 && columnaAux > 0) {
					columnaAux -= 1;
				}

				//Intercambiamos las columnas
				while (columna1>=0 && columnaAux>=0) {	//control de desbordo (de columna)
					tablero[(i*filas) + columna1].color = tablero[(i*filas) + columnaAux].color;
					tablero[(i*filas) + columnaAux].color = 0;
					columna1 = columna1 - columnas;
					columnaAux = columnaAux - columnas;
				}
			}
		}
	}
	
	
	return 0;
}

//Funcion que comprueba si dos diamantes son adyacentes
bool adyacentes(Diamante tablero, int fila1, int columna1, int fila2, int columna2) {
	bool ady = false;

	if (fila1 == fila2 + 1 || fila1 == fila2 - 1 || (fila1 == fila2 && columna1 != columna2)) {
		if (columna1 == columna2 + 1 || columna1 == columna2 - 1 || (columna1 == columna2 && fila1 != fila2)) {
			if ((fila1 == fila2 - 1 && columna1 == columna2 - 1) || (fila1 == fila2 + 1 && columna1 == columna2 - 1) ||	(fila1 == fila2 - 1 && columna1 == columna2 + 1) || (fila1 == fila2 + 1 && columna1 == columna2 + 1)) {
				ady = false; //Si el movimiento es en diagonal no sera valido
			}
			else { ady = true; }
		}
	}
	if (!ady) cout << "\nMOVIMIENTO ERRONEO, debe ser entre diamantes adyacentes y que no esten en diagonal";
	return ady;
}


//Funcion que comprueba si los diamantes explotan
bool explotan(Diamante *tablero, int f1, int c1, int f2, int c2, bool ady, int filas, int columnas) {
	
	bool expl = false;
	int contIgualesArriba1=0, contIgualesAbajo1=0, contIgualesDer1=0, contIgualesIzq1=0, contIgualesVert1=0 , contIgualesHoriz1=0;
	int contIgualesArriba2=0, contIgualesAbajo2=0, contIgualesDer2=0, contIgualesIzq2=0, contIgualesVert2=0, contIgualesHoriz2=0;

	//HAcemos el intercambio en la matriz para comprobar si se puede explotar
	int colorAux1 = tablero[(f1*filas) + c1].color;
	int colorAux2 = tablero[(f2*filas) + c2].color;
	tablero[(f1*filas) + c1].color = colorAux2;
	tablero[(f2*filas) + c2].color = colorAux1;

	//Comprobamos todas las direcciones posibles
	contIgualesArriba1 = comprobarIgualesPos(tablero, f1, c1, arriba, filas, columnas);
	contIgualesAbajo1 = comprobarIgualesPos(tablero, f1, c1, abajo, filas, columnas);
	contIgualesVert1 = comprobarIgualesPos(tablero, f1, c1, arriba, filas, columnas) + comprobarIgualesPos(tablero, f1, c1, abajo, filas, columnas);
	contIgualesIzq1 = comprobarIgualesPos(tablero, f1, c1, izquierda, filas, columnas);
	contIgualesDer1 = comprobarIgualesPos(tablero, f1, c1, derecha, filas, columnas);
	contIgualesHoriz1 = comprobarIgualesPos(tablero, f1, c1, izquierda, filas, columnas) + comprobarIgualesPos(tablero, f1, c1, derecha, filas, columnas);

	contIgualesArriba2 = comprobarIgualesPos(tablero, f2, c2, arriba, filas, columnas);
	contIgualesAbajo2 = comprobarIgualesPos(tablero, f2, c2, abajo, filas, columnas);
	contIgualesVert2 = comprobarIgualesPos(tablero, f2, c2, arriba, filas, columnas) + comprobarIgualesPos(tablero, f2, c2, abajo, filas, columnas);
	contIgualesIzq2 = comprobarIgualesPos(tablero, f2, c2, izquierda, filas, columnas);
	contIgualesDer2 = comprobarIgualesPos(tablero, f2, c2, derecha, filas, columnas);
	contIgualesHoriz2 = comprobarIgualesPos(tablero, f2, c2, izquierda, filas, columnas) + comprobarIgualesPos(tablero, f2, c2, derecha, filas, columnas);

	//En caso de que alguno de estos parametros sea 2 o m�s, al menos uno explota
	if (contIgualesArriba1>=2 || contIgualesAbajo1>=2 || contIgualesVert1>=2 || contIgualesIzq1>=2 || contIgualesDer1>=2 || contIgualesHoriz1>=2) {
		expl = true;
	}
	else if (contIgualesArriba2>=2 || contIgualesAbajo2>=2 || contIgualesVert2>=2 || contIgualesIzq2>=2 || contIgualesDer2>=2 || contIgualesHoriz2>=2) {
		expl = true;
	}
	// deshacemos los cambios en la matriz
		tablero[(f1*filas) + c1].color = colorAux1;
		tablero[(f2*filas) + c2].color = colorAux2;

	return expl;
}

//Funcion que pide movimiento hasta que sea correcto
bool movPosibleManual(Diamante *tablero, int filas, int columnas, string dificultad) {
	int f1 = 0, c1 = 0, f2 = 0, c2 = 0;
	bool expl = false, ady = false;
	string tipBomb = "";
	string guardar = "";

	cout << "\n--INTRODUCE TU JUGADA--";
	cout << "\nIntroduce la fila del primer diamante: ";
	cin >> f1;
	if (f1 == 99) { 
		cout << "\nQuieres guardar la partida? (si/no): ";
		cin >> guardar;
		if (guardar=="si") guardarPartida(tablero, filas, columnas, dificultad);
		exit(0); 
	}
	if (f1 == 91) {
		cout << "Activada bomba 1 ";
		//tipBomb = (char)f1;
		menuBomba(tablero, 91, filas, columnas);
	}
	else if (f1 == 92) {
		cout << "Activada bomba 2 ";
		//tipBomb = (char)f1;
		menuBomba(tablero, 92, filas, columnas);
	}
	else if (f1 == 93) {
		cout << "Activada bomba 3 ";
		//tipBomb = (char)f1;
		menuBomba(tablero, 93, filas, columnas);
	}
	else {
		cout << "Introduce la columna del primer diamante: ";
		cin >> c1;
		cout << "Introduce la fila del segundo diamante: ";
		cin >> f2;
		cout << "Introduce la columna del segundo diamante: ";
		cin >> c2;
		
		ady = adyacentes(*tablero, f1, c1, f2, c2);
		expl = explotan(tablero, f1, c1, f2, c2, ady, filas, columnas);
		
		if (!expl) cout << "\nMOVIMIENTO ERRONEO, los diamantes seleccionados no explotan";

		if (ady && expl) {
			int colorAux1 = tablero[(f1*filas) + c1].color;
			int colorAux2 = tablero[(f2*filas) + c2].color;
			tablero[(f1*filas) + c1].color = colorAux2;
			tablero[(f2*filas) + c2].color = colorAux1;
			if (comprobarIgualesDer(tablero, f1, c1, filas, columnas) + comprobarIgualesIzquierda(tablero, f1, c1, filas) >= 2 || comprobarIgualesAbajo(tablero, f1, c1, filas) + comprobarIgualesArriba(tablero, f1, c1, filas) >= 2) {
				tablero = comprobarCadena(tablero, f1, c1, filas, columnas);
			}
			else {
				tablero = comprobarCadena(tablero, f2, c2, filas, columnas);
			}
		}

		moverAbajo(tablero, filas, columnas);
	}
	return true;
}


Diamante *bomba1(Diamante *tablero, int filas, int columnas) {
	int fila = 0;

	cout << "Selecciona la fila que quieras explotar: ";
	cin >> fila;

	for (int i = fila; i < fila + 1; i++) {  //recorremos la fila que queremos explotar
		for (int j = 0; j < columnas; j++) {
			tablero[(i*filas) + j].color = 0;
		}
	}
	moverAbajo(tablero, filas, columnas);
	return tablero;
}

Diamante *bomba2(Diamante *tablero, int filas, int columnas) {
	int columna = 0;

	cout << "Selecciona la columna que quieras explotar: ";
	cin >> columna;

	for (int i = 0; i < filas; i++) {  //ponemos a 0 las posiciones que son de esa columna
			tablero[(i*filas) + columna].color = 0;
	}
	moverIzquierda(tablero, filas, columnas);
	return tablero;
}

Diamante *bomba3(Diamante *tablero, int filas, int columnas) {
	int colorAux = 0;
	for (int i = 1; i < filas; i++) {
		for (int j = 1; j < columnas; j++) {

			//Intercambiamos esquinas de cuadrdo
			if (j + 1 < columnas && i + 1 < filas) {
				colorAux = tablero[(i*filas) + (j - 1)].color;
				tablero[(i*filas) + (j - 1)].color = tablero[((i + 1)*filas) + j].color;
				tablero[((i + 1)*filas) + j].color = tablero[(i*filas) + (j + 1)].color;
				tablero[(i*filas) + (j + 1)].color = tablero[((i - 1)*filas) + j].color;
				tablero[((i - 1)*filas) + j].color = colorAux;
			}
			

			//Intercambiamos flor del cuadrado
			if (i + 1 < filas && j + 1 < columnas) {
				colorAux = tablero[((i - 1)*filas) + (j - 1)].color;
				tablero[((i - 1)*filas) + (j - 1)].color = tablero[((i + 1)*filas) + (j - 1)].color;
				tablero[((i + 1)*filas) + (j - 1)].color = tablero[((i + 1)*filas) + (j + 1)].color;
				tablero[((i + 1)*filas) + (j + 1)].color = tablero[((i - 1)*filas) + (j + 1)].color;
				tablero[((i - 1)*filas) + (j + 1)].color = colorAux;
			}
			
			j = j +2;
		}
		i = i +2;
	}
	return tablero;
}

Diamante *menuBomba(Diamante *tablero, int tipBomba, int filas, int columnas) {
	int numTipoBomba = 0;

	if (tipBomba == 91) numTipoBomba = 1;
	else if (tipBomba == 92) numTipoBomba = 2;
	else numTipoBomba = 3;

	switch (numTipoBomba) {
		case 1: bomba1(tablero, filas, columnas);
			break;
		case 2: bomba2(tablero, filas, columnas);
			break;
		case 3: bomba3(tablero, filas, columnas);
			break;
	}
	return tablero;
}
Diamante *autoMov(Diamante *tablero, int movOptimoFila1, int movOptimoColumna1, int movOptimoFila2, int movOptimoColumna2, int filas, int columnas) {
	int colorAux1 = tablero[(movOptimoFila1*filas) + movOptimoColumna1].color;
	int colorAux2 = tablero[(movOptimoFila2*filas) + movOptimoColumna2].color;

	tablero[(movOptimoFila1*filas) + movOptimoColumna1].color = colorAux2;
	tablero[(movOptimoFila2*filas) + movOptimoColumna2].color = colorAux1;

	if (comprobarIgualesDer(tablero, movOptimoFila1, movOptimoColumna1, filas, columnas) + comprobarIgualesIzquierda(tablero, movOptimoFila1, movOptimoColumna1, filas) >= 2
		|| comprobarIgualesAbajo(tablero, movOptimoFila1, movOptimoColumna1, filas) + comprobarIgualesArriba(tablero, movOptimoFila1, movOptimoColumna1, filas) >= 2) {
		tablero = comprobarCadena(tablero, movOptimoFila1, movOptimoColumna1, filas, columnas);
	}
	else {
		tablero = comprobarCadena(tablero, movOptimoFila2, movOptimoColumna2, filas, columnas);
	}

	moverAbajo(tablero, filas, columnas);

	return tablero;
}
int autoContMov(Diamante *tablero, int fila1, int columna1, int fila2, int columna2, int filas, int columnas) {
	int comFilas1, comColum1,comFilas2,comColum2 = 0;
	
	int colorAux1 = tablero[(fila1*filas) + columna1].color;
	int colorAux2 = tablero[(fila2*filas) + columna2].color;

	tablero[(fila1*filas) + columna1].color = colorAux2;
	tablero[(fila2*filas) + columna2].color = colorAux1;

	comFilas1 = comprobarIgualesDer(tablero, fila1, columna1, filas, columnas) + comprobarIgualesIzquierda(tablero, fila1, columna1, filas);
	comColum1 = comprobarIgualesAbajo(tablero, fila1, columna1, filas) + comprobarIgualesArriba(tablero, fila1, columna1, filas);

	comFilas2 = comprobarIgualesDer(tablero, fila2, columna2, filas, columnas) + comprobarIgualesIzquierda(tablero, fila2, columna2, filas);
	comColum2 = comprobarIgualesAbajo(tablero, fila2, columna2, filas) + comprobarIgualesArriba(tablero, fila2, columna2, filas);

	tablero[(fila1*filas) + columna1].color = colorAux1;
	tablero[(fila2*filas) + columna2].color = colorAux2;

	if (comFilas1 > comFilas2 || comColum1 > comColum2) {
		if (comFilas1 > comColum1) {
			return comFilas1;
		}
		else return comColum1;
	}
	else {
		if (comFilas2 > comColum2) {
			return comFilas2;
		}
		else return comColum2;
	}
}

Diamante *movManualMode(Diamante *tablero, string opcionBomba, HANDLE hConsole, int filas, int columnas, string dificultad) {
	
	while (true) {

		bool mov = false;
		cout << "\n\n";
		SetConsoleTextAttribute(hConsole, 15);

		cout << "Introduce 99 para salir ";
		if (!hasMoreMovements(tablero, filas, columnas)) {
			cout << "\nNo hay movimiento disponibles\nQuieres usar una bomba? ";
			cin >> opcionBomba;
		}


		if (opcionBomba == "si") {
			cout << "Selecciona el tipo de bomba: ";
			int tipoBomba;
			cin >> tipoBomba;
			menuBomba(tablero, tipoBomba, filas, columnas);
			opcionBomba = "no";
		}
		else if (opcionBomba == "no") {
			while (!mov) {

				bool canMove = hasMoreMovements(tablero, filas, columnas);
				mov = movPosibleManual(tablero,filas,columnas,dificultad);
			}
		}
		else if (opcionBomba == "99") {
			exit(0);
		}

		if (dificultad == "F") {
			nColores = 4;
		}
		else if (dificultad == "M") {
			nColores = 6;
		}

		tablero = rellenarCeros(tablero,nColores, filas, columnas);

		cout << "\n\n";
		printDiamante(tablero, hConsole, filas, columnas);
		//getchar(); 
	}
}

Diamante *movAutomaticMode(Diamante *tablero, int filas, int columnas) {
	int posX = 0;
	int movOptimoFila1, movOptimoColumna1, movOptimoFila2, movOptimoColumna2, contMovOptimo = 0;
	int contDiamantesExplot = 0;
	HANDLE h;
	h = GetStdHandle(STD_OUTPUT_HANDLE);
	if (!hasMoreMovements(tablero, filas, columnas)) {
		SetConsoleTextAttribute(h, 15);
		cout << "\nNo hay movimiento disponibles\n ";
		cout << "Selecciona el tipo de bomba: ";
		int tipoBomba;
		cin >> tipoBomba;
		menuBomba(tablero, tipoBomba, filas, columnas);
	}
	else {
		while (posX < filas) {
				for (int posY = 0; posY < columnas; posY++) {
					if (posX + 1 < filas && explotan(tablero, posX, posY, posX + 1, posY, true, filas, columnas)) { // Abajo
						contDiamantesExplot = autoContMov(tablero, posX, posY, posX + 1, posY, filas, filas);
						if (contDiamantesExplot >= contMovOptimo) {
								contMovOptimo = contDiamantesExplot;
								movOptimoFila1 = posX;
								movOptimoColumna1 = posY;
								movOptimoFila2 = posX + 1;
								movOptimoColumna2 = posY;
						}
					}
					else if (posY + 1 < columnas && explotan(tablero, posX, posY, posX, posY + 1, true, filas, columnas)) { //Derecha
						contDiamantesExplot = autoContMov(tablero, posX, posY, posX, posY+1, filas, filas);
						if (contDiamantesExplot >= contMovOptimo) {
							contMovOptimo = contDiamantesExplot;
							movOptimoFila1 = posX;
							movOptimoColumna1 = posY;
							movOptimoFila2 = posX;
							movOptimoColumna2 = posY+1;
						}
					}
					else if (posY - 1 >= 0 && explotan(tablero, posX, posY, posX, posY - 1, true, filas, columnas)) { //Izquierda
						contDiamantesExplot = autoContMov(tablero, posX, posY, posX, posY-1, filas, filas);
						if (contDiamantesExplot > contMovOptimo) {
							contMovOptimo = contDiamantesExplot;
							movOptimoFila1 = posX;
							movOptimoColumna1 = posY;
							movOptimoFila2 = posX;
							movOptimoColumna2 = posY-1;
						}
					}
					else if (posX - 1 >= 0 && explotan(tablero, posX, posY, posX - 1, posY, true, filas, columnas)) {//Arriba
						contDiamantesExplot = autoContMov(tablero, posX, posY, posX-1, posY, filas, filas);
						if (contDiamantesExplot >= contMovOptimo) {
							contMovOptimo = contDiamantesExplot;
							movOptimoFila1 = posX;
							movOptimoColumna1 = posY;
							movOptimoFila2 = posX - 1;
							movOptimoColumna2 = posY;
						}
					}
				}
				posX++;
			}
	
	SetConsoleTextAttribute(h, 15);

	cout << "\nLa posicion mas optima es cambiar la fila y columna " << movOptimoFila1 << " " << movOptimoColumna1
		<< " por " << movOptimoFila2 << " " << movOptimoColumna2 << " Este movimiento explotara : " << contMovOptimo << endl;
	getchar();
	tablero = autoMov(tablero, movOptimoFila1, movOptimoColumna1, movOptimoFila2, movOptimoColumna2, filas, columnas);
	}
	return tablero;

}

//Metodo para guardar la partida en txt
void guardarPartida(Diamante *tablero, int filas, int columnas, string dificultad) {
	
	ofstream doc;
	doc.open("guardado.txt");
	doc << filas << "\n";
	doc << columnas << "\n";
	doc << dificultad << "\n";
	for (int i = 0; i < (filas*columnas); i++) {
		doc << tablero[i].color;
	}
	doc.close();
	cout << "Partida guardada";
	Sleep(1000);
	system("exit");

}

//Metodo para cargar la partida guardada en el txt
void cargarPartida() {
	
	HANDLE hConsole;
	hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
	const string fichero = "guardado.txt";
	ifstream leer;
	leer.open(fichero.c_str());
	string dificultad;
	Diamante *tablero;
	int i = 0, n = 48, filas = 0, columnas = 0;
	char fila[80];

	

	if (!leer.fail()) {
		leer.getline(fila, 80, '\n');
		while (n > 47 && n < 58) {
			n = (int)fila[i];
			i++;
			if (n > 47 && n < 58) {
				filas = filas * 10 + (n - 48);
			}
		}
	}

	n = 48;
	i = 0;
	if (!leer.fail()) {
		leer.getline(fila, 80, '\n');
		while (n > 47 && n < 58) {
			n = (int)fila[i];
			i++;
			if (n > 47 && n < 58) {
				columnas = columnas * 10 + (n - 48);
			}
		}
	}
	
	if (!leer.fail()) {
		leer.getline(fila,80, '\n');
		dificultad = fila[0];
	}
	
	int lectura = (filas*columnas)-1;
	tablero = new Diamante[filas*columnas];
	inicicializarArray(tablero, filas, columnas, nColores);
	
	for (int i = 0; i < filas*columnas; i++) {
		leer.getline(fila, 80, '\n');
		n = (int)fila[i];
		if (n > 47 && n < 58) {
			tablero[i].color = (int)fila[i] - 48;
		}
		
		
	}
	
	
	leer.close();
	
	printDiamante(tablero, hConsole, filas, columnas);
	movManualMode(tablero, "no", hConsole, filas, columnas, dificultad);
}


int main() {

	string modoJuego = "";
	string dificultad = "";
	string recuperar = "";
	int filas = 0;
	int columnas = 0;
	cout << "Que modo de juego desea iniciar? Automatico (A), Manual (M)" << endl;
	cin >> modoJuego;
	if (modoJuego == "m" || modoJuego == "M") {
		cout << "Quieres cargar la partida? (si/no) :";
		cin >> recuperar;
		if (recuperar == "si") { cargarPartida(); }
	}
	cout << "Introduzca las filas y columnas que desea para la matriz?\nFilas: ";
	cin >> filas;
	cout << "Columnas: ";
	cin >> columnas;
	cout << "Que dificultad desea para el juego? Facil(F), Medio(M), Dificil(D)" << endl;
	cin >> dificultad;

	if (dificultad == "F") {
		nColores = 4;
	}
	else if (dificultad == "M") {
		nColores = 6;
	} //Se pone por defecto en 8 colores


	string opcionBomba = "no";
	Diamante *tablero = new Diamante[filas*columnas];

	HANDLE hConsole;
	hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

	inicicializarArray(tablero,filas,columnas,nColores);
	//diam = comprobarIguales(diam);
	printDiamante(tablero, hConsole, filas, columnas);
	//guardarPartida();
	while (true) {
		if (modoJuego == "a" || modoJuego == "A" || modoJuego == "automatico") {
			movAutomaticMode(tablero,filas,columnas);
		}
		else {
			movManualMode(tablero, opcionBomba, hConsole, filas, columnas, dificultad);
		}
		int size = filas*columnas * sizeof(tablero);
		tablero = rellenarCeros(tablero,nColores, filas, columnas);

		cout << "\n\n";
		printDiamante(tablero, hConsole,filas,columnas);
		getchar();
	}
	free(tablero);
	getchar();
	return 0;

}



void printDiamante(Diamante *tablero, HANDLE hConsole, int filas, int columnas) {  //modulo para imprimir el tablero
	for (int i = 0; i<filas; i++) {
		for (int j = 0; j < columnas; j++) 
			tablero[i*filas + j].printDiamante(hConsole);
		cout << endl;
	}
	    
}

