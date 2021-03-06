import java.io.File;
import java.io.FileWriter;
import java.awt.{Color, Font}
import java.awt.BorderLayout
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.border.EmptyBorder
import scala.collection.mutable.ListBuffer

object jewelsLegend extends App{

 class Diamante (val pos:Int,val color:Int,val selected:Boolean)
   
 //Devuelve posiciones pulsadas por el usuario
 //diamante1 y diamante2 debe ser -1 inicialmente
 def devolverPulsados(tablero:List[Diamante], pos:Int, diamante1:Int, diamante2:Int):(Int,Int)={
   if(tablero.tail==Nil)return (diamante1,diamante2)
   else{
     if(tablero.head.selected==true){
       if(diamante1==(-1)){ 
         return devolverPulsados(tablero.tail,pos+1,tablero.head.pos,diamante2)
        }
       else return devolverPulsados(tablero.tail,pos+1,diamante1,tablero.head.pos)
       }
     else{
       return devolverPulsados(tablero.tail,pos+1,diamante1,diamante2)
     }
   }
 }
  
 //Devuelve true si hay 2 pulsados
 def dosPulsados(tablero:List[Diamante], pos:Int, cont:Int):Boolean={
   if(tablero.tail==Nil){
     if(cont==2) return true
     else return false
   }
   else{
     if(tablero.head.selected==true) return dosPulsados(tablero.tail,pos+1,cont+1)
     else return dosPulsados(tablero.tail,pos+1,cont)
   }
 }
 
  //funcion para pintar indicadores al tablero
def pintar_flechas_columnas(dificultad:Int) {
  if(dificultad==1){
    println("C O L U M N A S\n")
    println("0 1 2 3 4 5 6")
    println("| | | | | | |\n")
  }
  else{
    if(dificultad==2){
      println("C O L U M N A S\n")
      println("0 1 2 3 4 5 6 7 8 9 10")
      println("| | | | | | | | | | |\n")
    }
    else{
      println("C O L U M N A S\n")
      println("0 1 2 3 4 5 6 7 8 9 1011121314")
      println("| | | | | | | | | | | | | | |\n")
    }
  }
}
 
  //funcion parra convertir cada numero a color
def convertir_a_colores(valor:Int):Color = {
  if (valor==1)
    return Color.blue
  else if (valor==2)
    return Color.red
  else if (valor==3)
    return Color.yellow
  else if (valor==4)
    return Color.green
  else if (valor==5)
    return Color.pink
  else if (valor==6)
    return Color.black
  else if (valor==7)
    return Color.darkGray
  else return Color.white
}
  
 //Funncion que explota elementos iguales
  def explotar(ListaExplotar:List[Int], tablero:List[Diamante]):List[Diamante]={
    
      if(ListaExplotar.isEmpty){
        return tablero;
      }else{
        val tablero1 = insertar_diamante(0, ListaExplotar.head, tablero, ListaExplotar.head,false)
        return explotar(ListaExplotar.tail,tablero1);
      }
  }
  
 //Cambia los ceros por otro numero alatorio (OK)
  def reponer(dificultad:Int, tablero:List[Diamante],pos:Int):List[Diamante]={
    if(tablero.tail==Nil){return tablero}
    else{
      if(tablero.head.color==0){
        val rnd = scala.util.Random
        val diamante = new Diamante(pos,1+rnd.nextInt(colores_tablero(dificultad)),false)
        //println(diamante.pos,diamante.color)
        return diamante::reponer(dificultad,tablero.tail,pos+1)
      }
      else{
        return tablero.head::reponer(dificultad,tablero.tail,pos+1)
      }
    }
  }
  
  //Devuelve diamante de una posicion (OK)
  def devolverDiamanteLista(posicion:Int,tablero:List[Diamante]):Diamante={
    if(tablero.head.pos==posicion){return tablero.head}
    else devolverDiamanteLista(posicion,tablero.tail)
  }

  //Devuelve el numero de colores segun la dificultad (OK)
  def colores_tablero(dificultad:Int):Int={
    if(dificultad==1) return 4
    else{
      if(dificultad==2) return 6
      else return 7
    }
  }
  
  //Insertar un numero en una posicion (OK)
  def insertar_diamante(color:Int, pos:Int, lista:List[Diamante], posAux:Int,selected:Boolean):List[Diamante]={
    if(posAux==0){
      val diamante = new Diamante(pos, color,selected)
      diamante::lista.tail}
    else lista.head::insertar_diamante(color,pos,lista.tail,posAux-1,selected)
  }
  
  //genera una lista con los elementos que tienen que explotar en esta ronda.(OK)
  def generarListaIguales(tablero:List[Diamante], filas:Int, columnas:Int, pos:Int):List[Int]={
    if(pos <= filas*columnas-1){
      val horizontal = comprobarIgualesPos(pos,"derecha",0,filas,columnas,tablero) + comprobarIgualesPos(pos,"izquierda",0,filas,columnas,tablero)
      val vertical= comprobarIgualesPos(pos,"arriba",0,filas,columnas,tablero) + comprobarIgualesPos(pos,"abajo",0,filas,columnas,tablero)
         if((horizontal >= 2 || vertical >= 2) && obtenerColor(pos, tablero) != 0){
            return pos::generarListaIguales(tablero,filas,columnas,pos+1);
          }else{
            return generarListaIguales(tablero,filas,columnas,pos+1);
          }
     
    }else{
      return Nil;
    }
  }
  
  //Devuelve la cantidad de ceros en el tablero para poder calcular la puntuacion
  def getZero(tablero:List[Diamante], filas:Int, columnas:Int, pos:Int):Int={
    if(pos <= filas*columnas-1){
      if(devolverDiamanteLista(pos,tablero).color == 0){
        return 1 + getZero(tablero,filas,columnas,pos+1)
      }else return getZero(tablero,filas,columnas,pos+1)
    }else return 0
  }
  
 //Comprobar si un numero esta en la última columna (OK)
 //auxiliar inicial columnas-1
 def ultima_columna(posDiamante:Int,columnas:Int,aux:Int):Boolean={
   if(aux>posDiamante)  false
   else{
     if(posDiamante==aux) true
     else  ultima_columna(posDiamante,columnas,aux+columnas)
   }
 }
 
 //Comprobar si un numero esta en la primera columna (OK)
 //auxiliar inicial 0
 def primera_columna(posDiamante:Int,columnas:Int,aux:Int):Boolean={
   if(aux>posDiamante) false
   else{
     if(posDiamante==aux) true
     else  primera_columna(posDiamante,columnas,aux+columnas)
   }
 }

 //Comprobar si un numero esta en la primera fila (OK)
  def primera_fila(posDiamante:Int,columnas:Int):Boolean={
    if(posDiamante<columnas) true
    else false
  }
  
  //Comprobar si un numero esta en la ultima fila (OK)
  def ultima_fila(posDiamante:Int,columnas:Int,filas:Int):Boolean={
    if(posDiamante>=(filas*columnas)-columnas) true
    else false
  }
  
  
 //Comprueba que dos posiciones sean contiguas (OK)
  def diamantes_contiguos(pos1:Int, pos2:Int, filas:Int, columnas:Int):Boolean={
   
    //Poicion central
   if(!primera_columna(pos1,columnas,0) && !ultima_columna(pos1,columnas,columnas-1) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*Mirar derecha izquierda arriba abajo*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2+columnas) || (pos1==pos2-columnas))  true
     else false
   }
   
   //Posicion en ultima fila o columna
   else if(primera_fila(pos1,columnas) && !primera_columna(pos1,columnas,0) && !ultima_columna(pos1,columnas,columnas-1)){
     /*mirar abajo derecha izquierda*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2-columnas))  true
     else false
   }
   else if(ultima_fila(pos1,columnas,filas) && !primera_columna(pos1,columnas,0) && !ultima_columna(pos1,columnas,columnas-1)){
     /*mirar arriba derecha izquierda*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2+columnas))  true
     else false
   }
   else if(primera_columna(pos1,columnas,0) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*mirar arriba abajo derecha*/
     if ((pos1==pos2-1) || (pos1==pos2-columnas) || (pos1==pos2+columnas))  true
     else false
   }
   else if(ultima_columna(pos1,columnas,columnas-1) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*mirar arriba abajo izquierda*/
     if ((pos1==pos2+1) || (pos1==pos2-columnas) || (pos1==pos2+columnas))  true
     else false
   }
   
   //Posicion en esquina
   else if(primera_fila(pos1,columnas) && primera_columna(pos1,columnas,0)){
     /*mirar derecha y abajo*/
     if ((pos1==pos2-1) || (pos1==pos2-columnas))  true
     else false
   }
   else if(primera_fila(pos1,columnas) && ultima_columna(pos1,columnas,columnas-1)){
     /*mirar izquierda y abajo*/
     if ((pos1==pos2+1) || (pos1==pos2-columnas))  true
     else false  
   }
   else if(ultima_fila(pos1,columnas,filas) && primera_columna(pos1,columnas,0)){
     /*mirar derecha y arriba*/
     if ((pos1==pos2-1) || (pos1==pos2+columnas))  true
     else false
   }
   else if(ultima_fila(pos1,columnas,filas) && ultima_columna(pos1,columnas,columnas-1)){
     /*mirar izquierda y arriba*/
     if ((pos1==pos2+1) || (pos1==pos2+columnas))  true
     else false
   }
   else false
 }

//Comprueba el color de un diamante (OK)
  def obtenerColor(pos:Int, tablero:List[Diamante]):Int={
    if(tablero.head.pos==pos) tablero.head.color
    else  obtenerColor(pos,tablero.tail)
  }
  
//Comprobar el numero de iguales de una posicion (OK)
 def comprobarIgualesPos(pos:Int, direccion:String, cont:Int, filas:Int, columnas:Int, tablero:List[Diamante]):Int={
    if(direccion=="arriba" && !primera_fila(pos,columnas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos-columnas,tablero)){ comprobarIgualesPos(pos-columnas,direccion,cont+1,filas,columnas,tablero)}
      else {return cont}
    }
    else if(direccion=="abajo" && !ultima_fila(pos,columnas,filas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos+columnas,tablero)){ comprobarIgualesPos(pos+columnas,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else if(direccion=="derecha" && !ultima_columna(pos,columnas,columnas-1)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos+1,tablero)){ comprobarIgualesPos(pos+1,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else if(direccion=="izquierda" && !primera_columna(pos,columnas,0)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos-1,tablero)){ comprobarIgualesPos(pos-1,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else return cont
  }
 
  //Recorre el array en busca de ceros (OK)
def bucleMoverCeros(longTablero:Int,l:List[Diamante],aux:List[Diamante],filas:Int,columnas:Int):List[Diamante]={
  
   if(longTablero==0){
    
    if(devolverDiamanteLista(longTablero,l).color==0){
      val aux2=subir_ceros(longTablero,longTablero-columnas,aux,filas,columnas)
      return aux2
    }
    
    else return aux
    
  }
  else {
    if(devolverDiamanteLista(longTablero,l).color==0){
      val aux2=subir_ceros(longTablero,longTablero-columnas,aux,filas,columnas)
      bucleMoverCeros(longTablero-1,l,aux2,filas,columnas)
    }
    else bucleMoverCeros(longTablero-1,aux,aux,filas,columnas)}
  
}
   
  //Enocontrar 0's en una columna (OK)
def encontrarCero(posicion:Int,l:List[Diamante],columnas:Int):Int={
  if(posicion<0 || devolverDiamanteLista(posicion,l).color==0){return posicion}
  else encontrarCero(posicion-columnas,l,columnas)
}

  //Mueve los ceros hacia arriba  (OK)
def subir_ceros(pos0:Int,posIntercambio:Int,l:List[Diamante],filas:Int,columnas:Int):List[Diamante]={
  
  
  if((posIntercambio+columnas)-columnas<0)     return l
  else {
    
      if(devolverDiamanteLista(posIntercambio,l).color!=0){
        

        val diamAux1= devolverDiamanteLista(posIntercambio,l)
        val diamAux2= new Diamante(encontrarCero(pos0,l,columnas),0,false)
        val diamAux12=new Diamante(diamAux1.pos,diamAux2.color,false)
        val diamAux21=new Diamante(diamAux2.pos,diamAux1.color,false)
        
        //bajar el diamante que no es 0
        val aux=insertar_diamante(diamAux12.color,diamAux1.pos,l,diamAux1.pos,false)
        
        //poner el 0
        val x=insertar_diamante(diamAux21.color,diamAux2.pos,aux,diamAux2.pos,false)

        subir_ceros(pos0-columnas,posIntercambio,x,filas,columnas)//-columnas
        }
      else {
        subir_ceros(pos0,posIntercambio-columnas,l,filas,columnas)//pos0
      } 
  }
 }

//Cambia 2 columnas de una 'matriz'
def loopChangeColum(pos:Int,tablero:List[Diamante],columnas:Int):List[Diamante]={
  if(primera_columna(pos, columnas, 0)) return tablero;
  else{
    
    val diamante1=devolverDiamanteLista(pos, tablero);
    val diamante2=devolverDiamanteLista(pos-1,tablero)
    val diamante12= new Diamante(diamante1.pos,diamante2.color,false)
    val diamante21= new Diamante(diamante2.pos,diamante1.color,false)
    
    val aux=insertar_diamante(diamante12.color,diamante12.pos,tablero,diamante12.pos,false);
    
    val x=insertar_diamante(diamante21.color,diamante21.pos,aux,diamante21.pos,false);
    
    return loopChangeColum(pos+columnas,x,columnas);
  }
  
}

//Funcion para cambiar columnas
def moveLeft(pos:Int,tablero:List[Diamante],filas:Int,columnas:Int):List[Diamante]={
  if(pos>columnas-1) return tablero
  else {
      val firstDiamond=devolverDiamanteLista(pos,tablero);
      if(firstDiamond.color==0 && comprobarIgualesPos(pos, "abajo", 1, filas, columnas, tablero) == columnas && primera_columna(pos, columnas, pos)){

        val aux= loopChangeColum(pos,tablero,columnas);
        
        moveLeft(pos+1,aux,filas,columnas)
     
     } else moveLeft(pos+1,tablero,filas,columnas)
    
  }
}

 //Comprueba si un mov es posible segun los diamantes iguales (OK)
  def comprobarIguales(pos1:Int, pos2:Int, filas:Int, columnas:Int, tablero:List[Diamante]):Boolean={
    val arriba1 = comprobarIgualesPos(pos1,"arriba",0,filas,columnas,tablero)
    val arriba2 = comprobarIgualesPos(pos2,"arriba",0,filas,columnas,tablero)
    val abajo1 = comprobarIgualesPos(pos1,"abajo",0,filas,columnas,tablero)
    val abajo2 = comprobarIgualesPos(pos2,"abajo",0,filas,columnas,tablero)
    val derecha1 = comprobarIgualesPos(pos1,"derecha",0,filas,columnas,tablero)
    val derecha2 = comprobarIgualesPos(pos2,"derecha",0,filas,columnas,tablero)
    val izquierda1 = comprobarIgualesPos(pos1,"izquierda",0,filas,columnas,tablero)
    val izquierda2 = comprobarIgualesPos(pos2,"izquierda",0,filas,columnas,tablero)
    
    val horizontal1 = derecha1+izquierda1+1
    val vertical1 = arriba1+abajo1+1
    val horizontal2 = derecha2+izquierda2+1
    val vertical2 = arriba2+abajo2+1
    
    if(horizontal1>2 || horizontal2>2 || vertical1>2 || vertical2>2) true
    else false
  }
  
 //Comprobacion para saber si se puede realizar un movimiento (OK)
 def comprobarMovimiento(diamante1:Diamante, diamante2:Diamante, tablero:List[Diamante], filas:Int, columnas:Int):Boolean={
   val pos1 = diamante1.pos
   val pos2 = diamante2.pos
   val color1 = diamante1.color
   val color2 = diamante2.color
   
   //Comprobamos que esten contiguos
   val contiguo = diamantes_contiguos(pos1,pos2,filas,columnas)
   
   //Comprobamos que los iguales sean mayor que 2
   val tableroAux1 = insertar_diamante(color1,pos2,tablero,pos2,true)
   val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1,pos1,true)
   val iguales=comprobarIguales(pos1,pos2,filas,columnas,tableroAux2)
 
   if(!contiguo)  println("Error, diamantes no contiguos")
   if(!iguales)  println(("Error, diamantes no coincidentes"))
   return contiguo && iguales
 }
 
  
  //Intercambia dos diamantes sin comprobar (OK)
  def intercambiarSinComprobar(pos1:Int, pos2:Int, tablero:List[Diamante], filas:Int, columnas:Int):List[Diamante]={
    val color1 = devolverDiamanteLista(pos1:Int,tablero).color
    val color2 = devolverDiamanteLista(pos2:Int,tablero).color
    
    val tableroAux1 = insertar_diamante(color1,pos2,tablero,pos2,true)
    val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1,pos1,true)
    return tableroAux2    
  }
    //Bucle para ejecutar explotar 
  def loopDelete(tablero:List[Diamante],filas:Int, columnas:Int):List[Diamante]={
    
    val lista_eliminar = generarListaIguales(tablero, filas, columnas,0);
   
    if(lista_eliminar.isEmpty){
      return tablero
    }else{
      val tableroExplotado = explotar(lista_eliminar,tablero);
      val tableroCerosIzquierda = moveLeft(0, tableroExplotado, filas, columnas);
      val tableroCeros = bucleMoverCeros((filas*columnas)-1, tableroCerosIzquierda, tableroCerosIzquierda, filas, columnas);

      return loopDelete(tableroCeros,filas,columnas);
    }
  }
  //Modo intercambiar automatico
  def changeAutomatic(pos1:Int, pos2:Int, tablero:List[Diamante], filas:Int, columnas:Int):List[Diamante]={
    val color1 = devolverDiamanteLista(pos1,tablero).color
    val color2 = devolverDiamanteLista(pos2,tablero).color
    
    if(checkAutomaticMove(devolverDiamanteLista(pos1,tablero),devolverDiamanteLista(pos2,tablero), tablero, filas, columnas)){
      val tableroAux1 = insertar_diamante(color1,pos2,tablero,pos2,true)
      val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1,pos1,true)
      
      return tableroAux2;
    }
    else return tablero
  }
  
  //ComprobarMovimiento automatico
   def checkAutomaticMove(diamante1:Diamante, diamante2:Diamante, tablero:List[Diamante], filas:Int, columnas:Int):Boolean={
   val pos1 = diamante1.pos
   val pos2 = diamante2.pos
   val color1 = diamante1.color
   val color2 = diamante2.color
   
   //Comprobamos que esten contiguos
   val contiguo = diamantes_contiguos(pos1,pos2,filas,columnas)
   
   //Comprobamos que los iguales sean mayor que 2
   val tableroAux1 = insertar_diamante(color1,pos2,tablero,pos2,true)
   val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1,pos1,true)
   val iguales=comprobarIguales(pos1,pos2,filas,columnas,tableroAux2)
 
   return contiguo && iguales
 }
  //retorna la lista de explotados al cambiar las posiciones con el elemento de la derecha
   def returnListRight(pos:Int,tablero:List[Diamante], filas:Int, columnas:Int):List[Int]={
     
     if(ultima_columna(pos, columnas, columnas-1)){
       return Nil;
     }else{
       val tableroIntercambiarDerecha = changeAutomatic(pos, pos+1, tablero, filas, columnas);
       return generarListaIguales(tableroIntercambiarDerecha, filas, columnas, 0);
     }
   }
   
   //retorna la lista de explotados al cambiar las posiciones con el elemento de la izquierda
   def returnListLeft(pos:Int,tablero:List[Diamante], filas:Int, columnas:Int):List[Int]={
     
     if(primera_columna(pos, columnas, 0)){
       return Nil;
     }else{
        val tableroIntercambiarIzquierda = changeAutomatic(pos, pos-1, tablero, filas, columnas);
       return generarListaIguales(tableroIntercambiarIzquierda, filas, columnas, 0);
     }
   }
   
    //retorna la lista de explotados al cambiar las posiciones con el elemento de la abajo
   def returnListDown(pos:Int,tablero:List[Diamante], filas:Int, columnas:Int):List[Int]={
     if(ultima_fila(pos, columnas, filas)){
       return Nil;
     }else{
        val tableroIntercambiarAbajo = changeAutomatic(pos, pos+columnas, tablero, filas, columnas);
        return generarListaIguales(tableroIntercambiarAbajo, filas, columnas, 0);
     }
   }
   
  //retorna la lista de explotados al cambiar las posiciones con el elemento de la arriba
   def returnListUp(pos:Int,tablero:List[Diamante], filas:Int, columnas:Int):List[Int]={
     if(primera_fila(pos, columnas)){
       return Nil;
     }else{
        val tableroIntercambiarArriba = changeAutomatic(pos, pos-columnas, tablero, filas, columnas);
        return generarListaIguales(tableroIntercambiarArriba, filas, columnas, 0);
     }
   }
   //Funcion que retorna una lista con el mejor cambio para explotar 
  def returnGreaterList( tablero:List[Diamante], pos:Int, filas:Int, columnas:Int):(Int,Int)={

      val listaDerecha = returnListRight(pos, tablero, filas, columnas);
      val listaAbajo = returnListDown(pos, tablero, filas, columnas);
      val listaIzquierda = returnListLeft(pos, tablero, filas, columnas);
      val listaArriba = returnListUp(pos, tablero, filas, columnas);;
    
        if(listaDerecha.length > listaAbajo.length && listaDerecha.length > listaArriba.length && listaDerecha.length > listaIzquierda.length){
         return (pos,pos+1);
        }else if(listaIzquierda.length > listaAbajo.length && listaIzquierda.length > listaArriba.length){
          return (pos,pos-1);
        }else if(listaAbajo.length > listaArriba.length){
          return (pos,pos+columnas);
        }else if(!listaArriba.isEmpty){
          return (pos,pos-columnas);
        }else{
          return (-1,-1);
        }
    
  }
  def biggerFour(number1:Int,number2:Int,number3:Int,number4:Int):Int={
    if(number1>=number2 && number1 >= number3 && number1>= number4){
      return number1;
    }else if(number2 >= number3 && number2 >= number4){
      return number2;      
    }else if(number3 >= number4){
      return number3;
    }else return number4;
  }
  //MODO AUTOMATICO
  def automaticMode(contSame:Int,dificultad:Int,listaMayor:List[Int], tablero:List[Diamante], pos:Int, bestChange1:Int, bestChange2:Int,filas:Int, columnas:Int, score:Int):(List[Diamante],Int)={
    if(pos >= filas*columnas){
      val tableroAux = changeAutomatic(bestChange1, bestChange2, tablero, filas, columnas);
      println("*********************************************************************");
      println("* Mejor movimiento posicion : " + bestChange1 + " por posicion : " + bestChange2 + " y explotaran : "+contSame + " *");
      println("*********************************************************************");
      val boardScore = checkLoopDelete(tablero,dificultad,filas,columnas,score);
       if(boardScore._2>=4000){
         println("\n-- JUEGO TERMINADO --")
         return boardScore;
       }
       else return boardScore;
      
    }else { 
     val betterMove = returnGreaterList(tablero, pos, filas, columnas);
     if(betterMove._1 != -1 && betterMove._2 != -1){
      
       val tableroCambiado = changeAutomatic(betterMove._1, betterMove._2, tablero, filas, columnas);
       val bestList = generarListaIguales(tableroCambiado, filas, columnas, 0);
       
       val horizontal1 = comprobarIgualesPos(betterMove._1,"derecha",1,filas,columnas,tableroCambiado) + comprobarIgualesPos(betterMove._1,"izquierda",1,filas,columnas,tableroCambiado)
       val vertical1 = comprobarIgualesPos(betterMove._1,"arriba",1,filas,columnas,tableroCambiado) + comprobarIgualesPos(betterMove._1,"abajo",1,filas,columnas,tableroCambiado)
       
       val horizontal2 = comprobarIgualesPos(betterMove._2,"derecha",1,filas,columnas,tableroCambiado) + comprobarIgualesPos(betterMove._2,"izquierda",1,filas,columnas,tableroCambiado)
       val vertical2 = comprobarIgualesPos(betterMove._2,"arriba",1,filas,columnas,tableroCambiado) + comprobarIgualesPos(betterMove._2,"abajo",1,filas,columnas,tableroCambiado)
       
       val bestFour = biggerFour(horizontal1,horizontal2,vertical1,vertical2);
       
       if(bestList.length >= listaMayor.length && bestFour >= contSame){
         val reverseTablero = changeAutomatic(betterMove._2, betterMove._1, tableroCambiado, filas, columnas);
         return automaticMode(bestFour,dificultad,bestList, reverseTablero, pos+1, betterMove._1, betterMove._2, filas, columnas,score);
       }else{
          return automaticMode(contSame,dificultad,listaMayor, tablero, pos+1, bestChange1, bestChange2, filas, columnas,score)
       }
     }else{
       return automaticMode(contSame,dificultad,listaMayor, tablero, pos+1, bestChange1, bestChange2, filas, columnas,score)
     }
    }  
  }
  //generar un lista que sera el tablero (OK)
  def generarTablero(pos: Int, filas:Int, columnas:Int, dificultad:Int):List[Diamante]={
    if(pos==((filas*columnas)-1)){
      val rnd = scala.util.Random
      val diamante = new Diamante(pos,1+rnd.nextInt(colores_tablero(dificultad)),false)
      diamante::Nil
    }
    else{
      val rnd = scala.util.Random
      val diamante = new Diamante(pos,1+rnd.nextInt(colores_tablero(dificultad)),false)
      diamante::generarTablero(pos+1,filas,columnas,dificultad)
    }
  }
  
 def saveFile(puntuacion: Int, dificultad:Int): Unit = {
  println("\n\nHas conseguido " + puntuacion + " puntos.")
  println("¿Con qué nombre de usuario quieres guardar la puntuación? (Máximo 12 caracteres")
  val rl = readLine()
  if (rl != null){
      val fw = new FileWriter("puntuaciones.txt", true)
      if (rl.length()<8){
       fw.write("\n"+rl+"  "+"----> " + puntuacion + "------>" + dificultad)
       println("Se ha guardado la puntuacion de " + puntuacion + " puntos a nombre de " + rl)
   }
      if (rl.length()>7 && rl.length()<13){
       fw.write("\n"+rl+" "+"----> " + puntuacion)
       println("Se ha guardado la puntuacion de " + puntuacion + " puntos a nombre de " + rl)
   
      }else if (rl.length() > 12){
       println("\n Error: Has introducido más de 12 carácteres.")
       saveFile(puntuacion, dificultad)
     }
   fw.close()
  }   
 }
  
  //Blucle para jugada del usuario
  def bucleJugador(tablero:List[Diamante],dificultad:Int,filas:Int,columnas:Int,score:Int,pos1:Int,pos2:Int):(List[Diamante],Int)={
    
    //contents =  print_tablero(tablero, dificultad, columnas, filas,0)
       val tableroAux = intercambiarSinComprobar(pos1, pos2, tablero, filas, columnas)
       val boardScore = checkLoopDelete(tableroAux,dificultad,filas,columnas,score);
      
       if(boardScore._2>=4000){ 
         println("\n-- JUEGO TERMINADO --") 
         return boardScore
       }else{
         return boardScore
       }

    
  }
  
  def checkLoopDelete(tablero:List[Diamante],dificultad:Int,filas:Int, columnas:Int, score:Int): (List[Diamante],Int)={
    val tableroAux = loopDelete(tablero,filas,columnas)
    val Score = score + (getZero(tableroAux, filas, columnas, 0) * 25);
    val tableroFinal = reponer(dificultad, tableroAux, 0);

    if(generarListaIguales(tableroFinal, filas, columnas, 0).length == 0){
      return (tableroFinal, Score)
    }else{
      return checkLoopDelete(tableroFinal,dificultad,filas,columnas,Score);
    } 
  }
  
  
  
  def print_lista(tablero:List[Int], columnas:Int, cont:Int){
    if(!tablero.isEmpty){
      if(cont==columnas){
        print(tablero.head + "\n")
        print_lista(tablero.tail,columnas,1)
      }
      else{
        print(tablero.head + " ")
        print_lista(tablero.tail,columnas,cont+1)
      }
    }
    
  }
  
  def getLevel(dificultad:Int):(Int,Int)={
    if(dificultad == 1){
      return (7,9)
    }else if(dificultad ==2){
      return (11,17)
    }else{
      return (15,27)
    }
  }
  

}