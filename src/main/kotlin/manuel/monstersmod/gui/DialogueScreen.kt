package manuel.monstersmod.gui

import manuel.monstersmod.network.DialogueNetworking
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class DialogueScreen(
    private val nodeId: String,
    private val npcText: String,
    private val options: List<String>
) : Screen(Text.literal("Diálogo")) {

    // init() se ejecuta una vez cuando la pantalla se abre. widgth y height son propiedades que contienen el tamaño actual de la ventana del videojuego en pixeles.
    override fun init() {
        val centerX = width / 2
        val startY = height / 2 + 20

        // Un forEach pero que ademas te devuelve el indice de cada elemento del array. Con addDrawableChild() se registra un widget en la pantalla, sin el widget el boton no se dibuja y no responde a los clicks
        options.forEachIndexed { index, optionText ->
            addDrawableChild(
                ButtonWidget.builder(Text.literal(optionText)) { // Creamos un boton con el texto de esa opción

                    DialogueNetworking.sendDialogueChoice(nodeId, index) // Enviamos la info al servidor
                    client!!.setScreen(null) // cierra la pantalla

                    // cuando el jugador pulsa el botón
                    // aquí mandaremos el paquete cliente -> servidor
                }
                    .dimensions(centerX - 100, startY + (index * 25), 200, 20) // Definimos la posicion del boton
                    .build() // construimos el widget con todo configurado.
            )
        }
    }

    // Funcion que se llamará en cada frame para dibujar la GUI. Este es el bucle de renderizado de la pantalla.
    // DrawContext es el objeto con el que puedes dibujar texto, formas, texturas... mouseX y mouseY la posicion actual del ratón.
    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(drawContext) //renderBackground dibuja un fondo oscuro semitransparente. El objeto DrawContext es con el que trabajamos para modificar la GUI.

        drawContext.matrices.push() // Guarda el estado actual de la GUI
        drawContext.matrices.scale(1.0f, 1.0f, 1.0f) // Este metodo escala todo lo que se dibuje a partir de aqui. Primer parametro escala en X, segundo escala en Y y tercero escala en Z (profundidad).

        // Modificamos el objeto DrawContext para dibujar un texto horizontalmente centrado con efecto de sombra.
        drawContext.drawTextWrapped(
            textRenderer, // Le pasamos esto para convertir un Text en pixeles usando la fuente de Minecraft.
            Text.literal(npcText), // El texto que dibujaremos, en este caso lo que dice el NPC que llego a traves del paquete de red.
            width / 4, // La posicion x donde se centra el texto
            height / 4, // La posicion Y del texto,
            width / 2, // Ancho máximo antes de hacer salto de linea
            0xFFFFFF // El color del texto en hexadecimal
        )

        drawContext.matrices.pop() // Con push() hemos guardado el tamaño de la GUI del juego, con scale() modificamos el tamaño de la GUI de todo lo que se dibuje a continuación y con pop() volvemos al tamaño original.
        // Llamamos al metodo render() de la clase padre para renderizar toda la GUI. Le pasamos nuestro objeto de tipo DrawContext, la posicion del raton y el tiempo transcurrido desde el ultimo frame.
        super.render(drawContext, mouseX, mouseY, delta)
    }

    // Metodo de Screen para saber si se debe pausar el juego cuando se abre esa pantalla, le ponemos que no.
    override fun shouldPause() = false // el juego no se pausa al abrir el dialogo
}
