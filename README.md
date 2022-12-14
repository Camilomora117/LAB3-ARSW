
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?

Ejecutamos la clase Start Production:

![image](https://user-images.githubusercontent.com/98135134/186421316-84fc8ab9-e561-4d01-bdf2-ef87b137a649.png)

En el cual podemos observar que hace uso de una gran cantidad de recursos del PC, usando aproximadamente un 12% de su capacidad.
Las clases responsables del consumo es Consumer y Producer.

2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.

El consumo de recursos era alto debido a la cantidad de llamadas que realiza el consumidor, para esto hay que limitar esas llamadas:

![image](https://user-images.githubusercontent.com/98135134/186427856-06d414e1-0b96-4582-94b7-58d1c22b7f47.png)

Ahora el programa solo podra hacer una llamada cada 1 segundo. Asi el consumo de recursos baja drasticamente.

3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.

Para aumentar la produccion hay que disminuir el tiempo en el que se realiza cada peticion y aumentar el tiempo en el que se consume:
Pero para esto hay que tener en cuenta el stocklimit de la cola.

![image](https://user-images.githubusercontent.com/98135134/186434384-89561937-a22f-4b05-a121-1b685f679b88.png)

El consumo de recursos no aumenta mucho ya que estoy consumiendo lentamente:

![image](https://user-images.githubusercontent.com/98135134/186433907-04b93ca0-7fb5-46e9-a0c7-e9023d1a7dcc.png)


##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).

Creamos una clase controller la cual sincronizará los threads y llevará un registro global del numero de veces que una IP fue encontrada en una lista negra. El mecanismo usado en la implementación realizada nos permitirá aplicar temas expuestos en clase sobre como evitar los deadLocks. 

![image](https://user-images.githubusercontent.com/25957863/186451468-54b345d9-31f2-4c84-b114-429db2ee0e94.png)

- Lo anterior, garantizando que no se den condiciones de carrera.

Haciendo uso de el mecanismo de exclusión mutua podemos asegurar que los hilos que comparten el mismo recurso, en nuestro caso (ocurrencesCount) no ingresen a modificarlo o consultarlo al mismo tiempo.

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

Para el N correspondiente al número de inmortrales se tiene la variable "numOfImmortals" la cuál toma su valor de acuerdo al valor ingreasado en el JTextField de la clase ControlFrame. Seguido de esto se crean N inmortales y se agregan a una lista. Para que cada inmortal conozca a quien atacar se usa un random y una variable de verificación del index propio. El valor de la sumatoria de de los puntos de vida de todos los jugadores siempre debe ser (N * 100).

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

![image](https://user-images.githubusercontent.com/25957863/187224980-6a31c927-73bc-4ae0-b601-081304e83245.png)

No se cumple el invariante porque según la funcion para calcualr el valor del N que definimos en el apartado anterior. El valor de la sumatoria debería ser (5 * 100) = 500

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

Implementamos los siguientes dos métodos en la clase Immortal.java:

![image](https://user-images.githubusercontent.com/25957863/187555456-2bc84ab2-c706-430a-a04b-fb6f42a22a49.png)

Para la clase controlFrame usamos la dos métodos anteriormente implementados:

![image](https://user-images.githubusercontent.com/25957863/187555234-e39944c8-fca8-476d-ab37-c5cbc282342e.png)

![image](https://user-images.githubusercontent.com/25957863/187555272-a4b6fe7e-e576-4153-9e10-2bbb2b9850f0.png)

Seguido de esto modificamos el método run de la clase Thread, esto para que se sincronicen los hilos y los ponga a esperar mientras se haya presionado la el botón "Pause and check". 

![image](https://user-images.githubusercontent.com/25957863/187556261-e1a5cf51-c206-47d1-9585-4c3ed14d6867.png)

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

Si se cumple la invariante.

![image](https://user-images.githubusercontent.com/25957863/187559903-24d1c4cf-4916-4f0f-80b3-37118a73f301.png)

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```
![image](https://user-images.githubusercontent.com/25957863/187556425-1413ecee-0c93-4ca6-9a38-786d7821592a.png)

7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.

Al usar los programas mencionados nos damos cuenta que el programa se encuentra en un deadlock, entonces en algun punto los hilos se quedaron esperando.

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

Probamos el programa con 100:

![image](https://user-images.githubusercontent.com/98135134/187583241-48412bdd-3f32-4095-b6a9-278ce2624d3f.png)

Probamos el programa con 1000:

![image](https://user-images.githubusercontent.com/98135134/187583300-2fdcb254-e708-46c8-a5cb-398040ac3179.png)

Probamos con 10000:

![image](https://user-images.githubusercontent.com/98135134/187583361-1d990ce4-8e9b-44a6-a639-208e249082eb.png)

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

11. Para finalizar, implemente la opción STOP.

Agregamos el método stop en la clase immortal.

![image](https://user-images.githubusercontent.com/25957863/187561656-31176be3-4989-439b-a6bc-b26554758700.png)

Agregamos una validación simple al método run:

![image](https://user-images.githubusercontent.com/25957863/187561712-21384a12-ece3-4068-a988-fd0fbd695a69.png)

Agregamos el botón y su comportamiento en la clase ControlFrame.

![image](https://user-images.githubusercontent.com/25957863/187561498-bc09c05f-02f7-4aca-b411-30b3eeacbeb0.png)


<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
