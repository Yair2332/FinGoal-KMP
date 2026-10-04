package com.fingoal.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.gooli
import fingoal.sharedlogic.generated.resources.grenny
import fingoal.sharedlogic.generated.resources.rosy
import fingoal.sharedlogic.generated.resources.sun


/*
 * ============================================================
 * MODELO INTERNO DEL CHAT
 * ============================================================
 */

private data class AssistantMessage(
    val question: String,
    val answer: String
)


/*
 * ============================================================
 * CONFIGURACIÓN DE CADA PERSONAJE
 * ============================================================
 */

private data class AssistantCharacterConfig(
    val name: String,
    val color: Color
)


/*
 * ============================================================
 * COLORES DE LOS PERSONAJES
 *
 * Estos colores son propios de cada personaje y no cambian
 * con el tema claro/oscuro.
 * ============================================================
 */

private val GrennyColor = Color(0xFF62C8E8)
private val RosyColor = Color(0xFF8ED6A5)
private val SunColor = Color(0xFFFFA94D)
private val GooliColor = Color(0xFFA879E8)


/*
 * ============================================================
 * ASISTENTE PRINCIPAL
 * ============================================================
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantFinGoal(
    character: String,
    questions: List<AssistantQuestion>,
    modifier: Modifier = Modifier
) {

    /*
     * ========================================================
     * ESTADO DEL BOTTOM SHEET
     *
     * Se utiliza para abrir directamente expandido.
     * ========================================================
     */

    val sheetState = rememberModalBottomSheetState()

    var showAssistant by remember {
        mutableStateOf(false)
    }

    /*
     * ========================================================
     * CONVERSACIÓN
     * ========================================================
     */

    val conversation = remember {
        mutableStateListOf<AssistantMessage>()
    }

    /*
     * ========================================================
     * RECURSO DEL PERSONAJE
     * ========================================================
     */

    val characterResource: DrawableResource =
        when (character.lowercase()) {

            "grenny" ->
                Res.drawable.grenny

            "rosy" ->
                Res.drawable.rosy

            "sun" ->
                Res.drawable.sun

            "gooli" ->
                Res.drawable.gooli

            else ->
                Res.drawable.rosy
        }

    /*
     * ========================================================
     * CONFIGURACIÓN DEL PERSONAJE
     * ========================================================
     */

    val characterConfig =
        getCharacterConfig(character)


    /*
     * ========================================================
     * BOTÓN FLOTANTE
     * ========================================================
     */

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    end = 20.dp,
                    bottom = 5.dp
                )
                .size(64.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(characterConfig.color)
                .clickable {
                    showAssistant = true
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(characterResource),
                contentDescription = "Abrir asistente ${characterConfig.name}",
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }


    /*
     * ========================================================
     * MODAL DEL CHAT
     * ========================================================
     */

    if (showAssistant) {


        LaunchedEffect(Unit) {
            sheetState.expand()
        }

        ModalBottomSheet(
            sheetState = sheetState,

            onDismissRequest = {
                showAssistant = false
                conversation.clear()
            },

            containerColor =
                MaterialTheme.colorScheme.surface,

            contentColor =
                MaterialTheme.colorScheme.onSurface,

            tonalElevation = 0.dp

        ) {

            AssistantContent(

                character =
                    characterResource,

                characterConfig =
                    characterConfig,

                questions =
                    questions,

                conversation =
                    conversation,

                onClose = {
                    showAssistant = false
                    conversation.clear()
                }
            )
        }
    }
}


/*
 * ============================================================
 * CONFIGURACIÓN DE PERSONAJES
 * ============================================================
 */

private fun getCharacterConfig(
    character: String
): AssistantCharacterConfig {

    return when (character.lowercase()) {

        "grenny" -> {

            AssistantCharacterConfig(
                name = "Grenny",
                color = GrennyColor
            )
        }

        "rosy" -> {

            AssistantCharacterConfig(
                name = "Rosy",
                color = RosyColor
            )
        }

        "sun" -> {

            AssistantCharacterConfig(
                name = "Sun",
                color = SunColor
            )
        }

        "gooli" -> {

            AssistantCharacterConfig(
                name = "Gooli",
                color = GooliColor
            )
        }

        else -> {

            AssistantCharacterConfig(
                name = "Rosy",
                color = RosyColor
            )
        }
    }
}


/*
 * ============================================================
 * CONTENIDO DEL CHAT
 * ============================================================
 */

@Composable
private fun AssistantContent(
    character: DrawableResource,
    characterConfig: AssistantCharacterConfig,
    questions: List<AssistantQuestion>,
    conversation: SnapshotStateList<AssistantMessage>,
    onClose: () -> Unit
) {

    val scrollState =
        rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(650.dp)
    ) {


        /*
         * ====================================================
         * PERSONAJE GRANDE
         *
         * Queda detrás del contenedor.
         * ====================================================
         */

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = (-12).dp,
                    y = (-46).dp
                )
        ) {

            Image(
                painter =
                    painterResource(character),

                contentDescription =
                    characterConfig.name,

                modifier =
                    Modifier.size(160.dp),

                contentScale =
                    ContentScale.Fit
            )
        }


        /*
         * ====================================================
         * CONTENEDOR PRINCIPAL DEL CHAT
         * ====================================================
         */

        Surface(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 62.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),

            shape =
                RoundedCornerShape(28.dp),

            color =
                MaterialTheme.colorScheme.surface,

            contentColor =
                MaterialTheme.colorScheme.onSurface,

            shadowElevation = 8.dp

        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
            ) {


                /*
                 * ==========================================
                 * HEADER
                 * ==========================================
                 */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    /*
                     * Nombre del personaje.
                     */

                    Surface(

                        shape =
                            RoundedCornerShape(12.dp),

                        color =
                            characterConfig.color,

                        contentColor =
                            Color.White

                    ) {

                        Text(

                            text =
                                characterConfig.name,

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold,

                            modifier =
                                Modifier.padding(
                                    horizontal = 13.dp,
                                    vertical = 7.dp
                                )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )


                    /*
                     * Subtítulo.
                     */

                    Text(

                        text =
                            "Asistente financiero",

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,

                        modifier =
                            Modifier.weight(1f)
                    )


                    /*
                     * Botón cerrar.
                     */

                    Surface(

                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onClose()
                            },

                        shape =
                            CircleShape,

                        color =
                            Color.Transparent,

                        contentColor =
                            MaterialTheme.colorScheme.onSurfaceVariant

                    ) {

                        Text(

                            text = "✕",

                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,

                            modifier =
                                Modifier.padding(8.dp)
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                /*
                 * ==========================================
                 * CONVERSACIÓN
                 * ==========================================
                 */

                if (conversation.isEmpty()) {

                    AssistantWelcome(

                        character =
                            character,

                        characterConfig =
                            characterConfig
                    )

                } else {

                    /*
                     * Solo mostramos las últimas dos
                     * interacciones.
                     */

                    val visibleMessages =
                        conversation.takeLast(2)

                    visibleMessages.forEach { message ->

                        /*
                         * Mensaje del usuario.
                         */

                        UserMessage(

                            text =
                                message.question,

                            characterColor =
                                characterConfig.color
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        /*
                         * Respuesta del asistente.
                         */

                        AssistantMessageBubble(

                            character =
                                character,

                            characterConfig =
                                characterConfig,

                            text =
                                message.answer
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )
                    }
                }


                /*
                 * ==========================================
                 * SEPARACIÓN CHAT / PREGUNTAS
                 * ==========================================
                 */

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                HorizontalDivider(

                    color =
                        MaterialTheme.colorScheme.outlineVariant,

                    thickness =
                        1.dp
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                /*
                 * ==========================================
                 * TÍTULO PREGUNTAS
                 * ==========================================
                 */

                Text(

                    text =
                        "Preguntas",

                    color =
                        MaterialTheme.colorScheme.onSurface,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                /*
                 * ==========================================
                 * PREGUNTAS DISPONIBLES
                 * ==========================================
                 */

                val availableQuestions =
                    questions.filter { question ->

                        conversation.none {

                            it.question ==
                                    question.question
                        }
                    }


                if (availableQuestions.isNotEmpty()) {

                    Column(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        availableQuestions.forEach { question ->

                            QuestionButton(

                                question =
                                    question.question,

                                characterColor =
                                    characterConfig.color,

                                onClick = {

                                    conversation.add(

                                        AssistantMessage(

                                            question =
                                                question.question,

                                            answer =
                                                question.answer
                                        )
                                    )
                                }
                            )
                        }
                    }

                } else {

                    Text(

                        text =
                            "Ya respondiste todas las preguntas.",

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}


/*
 * ============================================================
 * MENSAJE DE BIENVENIDA
 * ============================================================
 */

@Composable
private fun AssistantWelcome(

    character: DrawableResource,

    characterConfig:
    AssistantCharacterConfig

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        Image(

            painter =
                painterResource(character),

            contentDescription =
                characterConfig.name,

            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),

            contentScale =
                ContentScale.Crop
        )


        Spacer(
            modifier =
                Modifier.width(8.dp)
        )


        Surface(

            shape =
                RoundedCornerShape(

                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),

            color =
                MaterialTheme.colorScheme.secondaryContainer,

            contentColor =
                MaterialTheme.colorScheme.onSecondaryContainer

        ) {

            Text(

                text =
                    "¡Hola! 👋 Elegí una pregunta y te ayudo con tus finanzas.",

                color =
                    MaterialTheme.colorScheme.onSecondaryContainer,

                modifier =
                    Modifier.padding(

                        horizontal = 14.dp,

                        vertical = 11.dp
                    )
            )
        }
    }
}


/*
 * ============================================================
 * MENSAJE DEL USUARIO
 * ============================================================
 */

@Composable
private fun UserMessage(

    text: String,

    characterColor: Color

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.End
    ) {

        /*
         * El usuario utiliza el color del personaje
         * como identidad de la conversación.
         */

        Surface(

            shape =
                RoundedCornerShape(

                    topStart = 16.dp,
                    topEnd = 4.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),

            color =
                characterColor,

            contentColor =
                Color.White

        ) {

            Text(

                text =
                    text,

                /*
                 * Texto blanco sobre el verde.
                 */

                color =
                    Color.White,

                fontWeight =
                    FontWeight.Medium,

                modifier =
                    Modifier.padding(

                        horizontal = 14.dp,

                        vertical = 10.dp
                    )
            )
        }
    }
}


/*
 * ============================================================
 * RESPUESTA DEL ASISTENTE
 * ============================================================
 */

@Composable
private fun AssistantMessageBubble(

    character: DrawableResource,

    characterConfig:
    AssistantCharacterConfig,

    text: String

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        Image(

            painter =
                painterResource(character),

            contentDescription =
                characterConfig.name,

            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),

            contentScale =
                ContentScale.Crop
        )


        Spacer(
            modifier =
                Modifier.width(8.dp)
        )


        /*
         * Burbuja del chatbot.
         *
         * Usamos los colores nativos de Material 3
         * para que se adapte automáticamente a
         * Light/Dark Theme.
         */

        Surface(

            shape =
                RoundedCornerShape(

                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),

            color =
                MaterialTheme.colorScheme.secondaryContainer,

            contentColor =
                MaterialTheme.colorScheme.onSecondaryContainer

        ) {

            Text(

                text =
                    text,

                color =
                    MaterialTheme.colorScheme.onSecondaryContainer,

                modifier =
                    Modifier.padding(

                        horizontal = 14.dp,

                        vertical = 10.dp
                    )
            )
        }
    }
}


/*
 * ============================================================
 * CARD DE PREGUNTA
 * ============================================================
 */

@Composable
private fun QuestionButton(

    question: String,

    characterColor: Color,

    onClick: () -> Unit

) {

    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(16.dp),

        /*
         * Material 3 se encarga de adaptar este
         * container al tema claro/oscuro.
         */

        color =
            MaterialTheme.colorScheme.surfaceContainerLow,

        contentColor =
            MaterialTheme.colorScheme.onSurface,

        border =
            BorderStroke(

                width = 1.dp,

                color =
                    MaterialTheme.colorScheme.outlineVariant
            ),

        shadowElevation =
            1.dp

    ) {

        Row(

            modifier =
                Modifier.padding(

                    horizontal = 15.dp,

                    vertical = 14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            /*
             * Indicador del personaje.
             */

            Box(

                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        characterColor
                    )
            )


            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )


            Text(

                text =
                    question,

                color =
                    MaterialTheme.colorScheme.onSurface,

                fontWeight =
                    FontWeight.Medium,

                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}