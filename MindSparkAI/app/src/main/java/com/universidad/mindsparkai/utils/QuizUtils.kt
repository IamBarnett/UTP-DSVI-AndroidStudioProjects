package com.universidad.mindsparkai.utils

import com.universidad.mindsparkai.data.models.QuizQuestion
import kotlin.random.Random

object QuizUtils {

    /**
     * Genera preguntas aleatorias para una materia específica
     */
    fun generateRandomQuestions(subject: String, difficulty: String, count: Int = 5): List<QuizQuestion> {
        val questionBank = getQuestionBank(subject, difficulty)
        return questionBank.shuffled().take(count)
    }

    /**
     * Obtiene el banco de preguntas para una materia y dificultad específica
     */
    private fun getQuestionBank(subject: String, difficulty: String): List<QuizQuestion> {
        return when (subject.lowercase()) {
            "matemáticas", "matematicas" -> getMathQuestions(difficulty)
            "química", "quimica" -> getChemistryQuestions(difficulty)
            "física", "fisica" -> getPhysicsQuestions(difficulty)
            "biología", "biologia" -> getBiologyQuestions(difficulty)
            "historia" -> getHistoryQuestions(difficulty)
            "literatura" -> getLiteratureQuestions(difficulty)
            "inglés", "ingles" -> getEnglishQuestions(difficulty)
            "filosofía", "filosofia" -> getPhilosophyQuestions(difficulty)
            "economía", "economia" -> getEconomicsQuestions(difficulty)
            "programación", "programacion" -> getProgrammingQuestions(difficulty)
            "psicología", "psicologia" -> getPsychologyQuestions(difficulty)
            "sociología", "sociologia" -> getSociologyQuestions(difficulty)
            "geografia", "geografía" -> getGeographyQuestions(difficulty)
            "arte" -> getArtQuestions(difficulty)
            "música", "musica" -> getMusicQuestions(difficulty)
            "derecho" -> getLawQuestions(difficulty)
            "medicina" -> getMedicineQuestions(difficulty)
            "ingeniería", "ingenieria" -> getEngineeringQuestions(difficulty)
            "arquitectura" -> getArchitectureQuestions(difficulty)
            "administración", "administracion" -> getBusinessQuestions(difficulty)
            else -> getGeneralQuestions(difficulty)
        }
    }

    private fun getMathQuestions(difficulty: String): List<QuizQuestion> {
        return when (difficulty.lowercase()) {
            "fácil", "facil" -> listOf(
                QuizQuestion(
                    id = "math_easy_1",
                    question = "¿Cuánto es 15 + 27?",
                    options = listOf("A) 40", "B) 42", "C) 44", "D) 46"),
                    correctAnswer = 1,
                    explanation = "15 + 27 = 42",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_2",
                    question = "¿Cuál es el área de un cuadrado con lado de 5 cm?",
                    options = listOf("A) 20 cm²", "B) 25 cm²", "C) 30 cm²", "D) 35 cm²"),
                    correctAnswer = 1,
                    explanation = "Área = lado × lado = 5 × 5 = 25 cm²",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_3",
                    question = "¿Cuánto es 8 × 7?",
                    options = listOf("A) 54", "B) 56", "C) 58", "D) 60"),
                    correctAnswer = 1,
                    explanation = "8 × 7 = 56",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_4",
                    question = "¿Cuántos grados tiene un triángulo?",
                    options = listOf("A) 90°", "B) 180°", "C) 270°", "D) 360°"),
                    correctAnswer = 1,
                    explanation = "La suma de los ángulos internos de un triángulo es siempre 180°",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_5",
                    question = "¿Cuál es la raíz cuadrada de 64?",
                    options = listOf("A) 6", "B) 7", "C) 8", "D) 9"),
                    correctAnswer = 2,
                    explanation = "√64 = 8, porque 8 × 8 = 64",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_6",
                    question = "¿Cuánto es 100 ÷ 4?",
                    options = listOf("A) 20", "B) 25", "C) 30", "D) 35"),
                    correctAnswer = 1,
                    explanation = "100 ÷ 4 = 25",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_easy_7",
                    question = "¿Qué número sigue en la secuencia: 2, 4, 6, 8, ...?",
                    options = listOf("A) 9", "B) 10", "C) 11", "D) 12"),
                    correctAnswer = 1,
                    explanation = "Es una secuencia de números pares, por lo que sigue 10",
                    subject = "Matemáticas",
                    difficulty = difficulty
                )
            )
            "intermedio" -> listOf(
                QuizQuestion(
                    id = "math_inter_1",
                    question = "¿Cuál es la derivada de x²?",
                    options = listOf("A) x", "B) 2x", "C) x²", "D) 2x²"),
                    correctAnswer = 1,
                    explanation = "La derivada de x² es 2x usando la regla de potencias",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_inter_2",
                    question = "Resolver: 3x + 5 = 17",
                    options = listOf("A) x = 3", "B) x = 4", "C) x = 5", "D) x = 6"),
                    correctAnswer = 1,
                    explanation = "3x = 17 - 5 = 12, por lo tanto x = 4",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_inter_3",
                    question = "¿Cuál es el valor de sin(90°)?",
                    options = listOf("A) 0", "B) 1", "C) -1", "D) 1/2"),
                    correctAnswer = 1,
                    explanation = "El seno de 90° es 1",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_inter_4",
                    question = "¿Cuál es la fórmula del área de un círculo?",
                    options = listOf("A) πr", "B) πr²", "C) 2πr", "D) πd"),
                    correctAnswer = 1,
                    explanation = "El área de un círculo es π × radio²",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_inter_5",
                    question = "¿Cuánto es log₁₀(100)?",
                    options = listOf("A) 1", "B) 2", "C) 10", "D) 100"),
                    correctAnswer = 1,
                    explanation = "log₁₀(100) = 2 porque 10² = 100",
                    subject = "Matemáticas",
                    difficulty = difficulty
                )
            )
            else -> listOf( // Difícil
                QuizQuestion(
                    id = "math_hard_1",
                    question = "¿Cuál es la integral de sin(x)?",
                    options = listOf("A) cos(x)", "B) -cos(x)", "C) sin(x)", "D) -sin(x)"),
                    correctAnswer = 1,
                    explanation = "La integral de sin(x) es -cos(x) + C",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_hard_2",
                    question = "¿Cuál es el límite de (sin x)/x cuando x tiende a 0?",
                    options = listOf("A) 0", "B) 1", "C) ∞", "D) No existe"),
                    correctAnswer = 1,
                    explanation = "Es un límite fundamental que vale 1",
                    subject = "Matemáticas",
                    difficulty = difficulty
                ),
                QuizQuestion(
                    id = "math_hard_3",
                    question = "¿Cuál es la transformada de Laplace de e^(at)?",
                    options = listOf("A) 1/(s-a)", "B) 1/(s+a)", "C) s/(s-a)", "D) a/(s-a)"),
                    correctAnswer = 0,
                    explanation = "L{e^(at)} = 1/(s-a) para s > a",
                    subject = "Matemáticas",
                    difficulty = difficulty
                )
            )
        }
    }

    private fun getChemistryQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "chem_1",
                question = "¿Cuál es el símbolo químico del oro?",
                options = listOf("A) Go", "B) Au", "C) Ag", "D) Or"),
                correctAnswer = 1,
                explanation = "Au viene del latín 'aurum'",
                subject = "Química",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "chem_2",
                question = "¿Cuántos electrones tiene el átomo de carbono?",
                options = listOf("A) 4", "B) 6", "C) 8", "D) 12"),
                correctAnswer = 1,
                explanation = "El carbono tiene número atómico 6, por lo que tiene 6 electrones",
                subject = "Química",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "chem_3",
                question = "¿Cuál es la fórmula del agua?",
                options = listOf("A) H2O", "B) HO2", "C) H3O", "D) H2O2"),
                correctAnswer = 0,
                explanation = "El agua está formada por dos átomos de hidrógeno y uno de oxígeno",
                subject = "Química",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "chem_4",
                question = "¿Qué tipo de enlace forma el NaCl?",
                options = listOf("A) Covalente", "B) Iónico", "C) Metálico", "D) Van der Waals"),
                correctAnswer = 1,
                explanation = "El cloruro de sodio forma enlaces iónicos",
                subject = "Química",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "chem_5",
                question = "¿Cuál es el pH del agua pura?",
                options = listOf("A) 6", "B) 7", "C) 8", "D) 9"),
                correctAnswer = 1,
                explanation = "El agua pura tiene pH neutro = 7",
                subject = "Química",
                difficulty = difficulty
            )
        )
    }

    private fun getPhysicsQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "phys_1",
                question = "¿Cuál es la velocidad de la luz en el vacío?",
                options = listOf("A) 300,000 km/s", "B) 300,000,000 m/s", "C) 3×10⁸ m/s", "D) Todas las anteriores"),
                correctAnswer = 3,
                explanation = "La velocidad de la luz es aproximadamente 3×10⁸ m/s",
                subject = "Física",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "phys_2",
                question = "¿Cuál es la segunda ley de Newton?",
                options = listOf("A) F = ma", "B) F = mv", "C) F = mc²", "D) F = mgh"),
                correctAnswer = 0,
                explanation = "La segunda ley de Newton establece que F = ma",
                subject = "Física",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "phys_3",
                question = "¿Qué es la energía cinética?",
                options = listOf("A) mgh", "B) ½mv²", "C) mc²", "D) kx²"),
                correctAnswer = 1,
                explanation = "La energía cinética es ½mv²",
                subject = "Física",
                difficulty = difficulty
            )
        )
    }

    private fun getBiologyQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "bio_1",
                question = "¿Cuál es la principal función de las mitocondrias?",
                options = listOf("A) Síntesis de proteínas", "B) Producción de energía (ATP)", "C) Almacenamiento de ADN", "D) Digestión celular"),
                correctAnswer = 1,
                explanation = "Las mitocondrias son las centrales energéticas de la célula",
                subject = "Biología",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "bio_2",
                question = "¿Dónde se encuentra el ADN en las células eucariotas?",
                options = listOf("A) Citoplasma", "B) Núcleo", "C) Ribosomas", "D) Mitocondrias"),
                correctAnswer = 1,
                explanation = "En las células eucariotas, el ADN se encuentra principalmente en el núcleo",
                subject = "Biología",
                difficulty = difficulty
            )
        )
    }

    private fun getHistoryQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "hist_1",
                question = "¿En qué año comenzó la Segunda Guerra Mundial?",
                options = listOf("A) 1938", "B) 1939", "C) 1940", "D) 1941"),
                correctAnswer = 1,
                explanation = "La Segunda Guerra Mundial comenzó en 1939",
                subject = "Historia",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "hist_2",
                question = "¿Quién fue el primer presidente de Estados Unidos?",
                options = listOf("A) Thomas Jefferson", "B) George Washington", "C) John Adams", "D) Benjamin Franklin"),
                correctAnswer = 1,
                explanation = "George Washington fue el primer presidente de Estados Unidos",
                subject = "Historia",
                difficulty = difficulty
            )
        )
    }

    private fun getLiteratureQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "lit_1",
                question = "¿Quién escribió 'Cien años de soledad'?",
                options = listOf("A) Mario Vargas Llosa", "B) Gabriel García Márquez", "C) Julio Cortázar", "D) Isabel Allende"),
                correctAnswer = 1,
                explanation = "Gabriel García Márquez escribió 'Cien años de soledad'",
                subject = "Literatura",
                difficulty = difficulty
            )
        )
    }

    private fun getEnglishQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "eng_1",
                question = "What is the past tense of 'go'?",
                options = listOf("A) goed", "B) went", "C) gone", "D) going"),
                correctAnswer = 1,
                explanation = "The past tense of 'go' is 'went'",
                subject = "Inglés",
                difficulty = difficulty
            )
        )
    }

    private fun getPhilosophyQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "phil_1",
                question = "¿Quién dijo 'Pienso, luego existo'?",
                options = listOf("A) Platón", "B) Aristóteles", "C) René Descartes", "D) Immanuel Kant"),
                correctAnswer = 2,
                explanation = "René Descartes acuñó la famosa frase 'Cogito ergo sum' (Pienso, luego existo)",
                subject = "Filosofía",
                difficulty = difficulty
            )
        )
    }

    private fun getEconomicsQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "econ_1",
                question = "¿Qué es la inflación?",
                options = listOf("A) Aumento general de precios", "B) Disminución de precios", "C) Aumento del empleo", "D) Disminución del PIB"),
                correctAnswer = 0,
                explanation = "La inflación es el aumento generalizado y sostenido de los precios",
                subject = "Economía",
                difficulty = difficulty
            )
        )
    }

    private fun getProgrammingQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "prog_1",
                question = "¿Qué significa HTML?",
                options = listOf("A) HyperText Markup Language", "B) High Tech Modern Language", "C) Home Tool Markup Language", "D) Hyperlink Text Markup Language"),
                correctAnswer = 0,
                explanation = "HTML significa HyperText Markup Language",
                subject = "Programación",
                difficulty = difficulty
            )
        )
    }

    private fun getPsychologyQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "psych_1",
                question = "¿Quién es considerado el padre del psicoanálisis?",
                options = listOf("A) Carl Jung", "B) Sigmund Freud", "C) B.F. Skinner", "D) Jean Piaget"),
                correctAnswer = 1,
                explanation = "Sigmund Freud es considerado el padre del psicoanálisis",
                subject = "Psicología",
                difficulty = difficulty
            )
        )
    }

    private fun getSociologyQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "soc_1",
                question = "¿Quién acuñó el término 'sociología'?",
                options = listOf("A) Max Weber", "B) Émile Durkheim", "C) Auguste Comte", "D) Karl Marx"),
                correctAnswer = 2,
                explanation = "Auguste Comte acuñó el término 'sociología' en el siglo XIX",
                subject = "Sociología",
                difficulty = difficulty
            )
        )
    }

    private fun getGeographyQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "geo_1",
                question = "¿Cuál es el río más largo del mundo?",
                options = listOf("A) Amazonas", "B) Nilo", "C) Mississippi", "D) Yangtze"),
                correctAnswer = 1,
                explanation = "El río Nilo es considerado el más largo del mundo con aproximadamente 6,650 km",
                subject = "Geografía",
                difficulty = difficulty
            )
        )
    }

    private fun getArtQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "art_1",
                question = "¿Quién pintó 'La Mona Lisa'?",
                options = listOf("A) Miguel Ángel", "B) Leonardo da Vinci", "C) Pablo Picasso", "D) Vincent van Gogh"),
                correctAnswer = 1,
                explanation = "Leonardo da Vinci pintó 'La Mona Lisa' entre 1503 y 1519",
                subject = "Arte",
                difficulty = difficulty
            )
        )
    }

    private fun getMusicQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "music_1",
                question = "¿Cuántas sinfonías compuso Beethoven?",
                options = listOf("A) 7", "B) 8", "C) 9", "D) 10"),
                correctAnswer = 2,
                explanation = "Ludwig van Beethoven compuso 9 sinfonías",
                subject = "Música",
                difficulty = difficulty
            )
        )
    }

    private fun getLawQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "law_1",
                question = "¿Qué significa 'habeas corpus'?",
                options = listOf("A) Tengas el cuerpo", "B) Derecho a la vida", "C) Presunción de inocencia", "D) Debido proceso"),
                correctAnswer = 0,
                explanation = "'Habeas corpus' significa literalmente 'tengas el cuerpo' y garantiza la libertad individual",
                subject = "Derecho",
                difficulty = difficulty
            )
        )
    }

    private fun getMedicineQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "med_1",
                question = "¿Cuál es el órgano más grande del cuerpo humano?",
                options = listOf("A) Hígado", "B) Pulmones", "C) Piel", "D) Cerebro"),
                correctAnswer = 2,
                explanation = "La piel es el órgano más grande del cuerpo humano",
                subject = "Medicina",
                difficulty = difficulty
            )
        )
    }

    private fun getEngineeringQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "eng_1",
                question = "¿Qué ley describe la relación entre voltaje, corriente y resistencia?",
                options = listOf("A) Ley de Newton", "B) Ley de Ohm", "C) Ley de Faraday", "D) Ley de Kirchhoff"),
                correctAnswer = 1,
                explanation = "La Ley de Ohm establece que V = I × R",
                subject = "Ingeniería",
                difficulty = difficulty
            )
        )
    }

    private fun getArchitectureQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "arch_1",
                question = "¿Qué arquitecto diseñó la Casa de la Cascada?",
                options = listOf("A) Frank Lloyd Wright", "B) Le Corbusier", "C) Mies van der Rohe", "D) Antoni Gaudí"),
                correctAnswer = 0,
                explanation = "Frank Lloyd Wright diseñó la famosa Casa de la Cascada en 1935",
                subject = "Arquitectura",
                difficulty = difficulty
            )
        )
    }

    private fun getBusinessQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "bus_1",
                question = "¿Qué significa ROI en finanzas?",
                options = listOf("A) Return on Investment", "B) Rate of Interest", "C) Risk of Investment", "D) Revenue on Income"),
                correctAnswer = 0,
                explanation = "ROI significa Return on Investment (Retorno de la Inversión)",
                subject = "Administración",
                difficulty = difficulty
            )
        )
    }

    private fun getGeneralQuestions(difficulty: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "gen_1",
                question = "¿Cuál es la capital de Francia?",
                options = listOf("A) Londres", "B) París", "C) Roma", "D) Madrid"),
                correctAnswer = 1,
                explanation = "París es la capital de Francia",
                subject = "General",
                difficulty = difficulty
            ),
            QuizQuestion(
                id = "gen_2",
                question = "¿Cuántos continentes hay?",
                options = listOf("A) 5", "B) 6", "C) 7", "D) 8"),
                correctAnswer = 2,
                explanation = "Hay 7 continentes: África, Antártida, Asia, Europa, América del Norte, Oceanía y América del Sur",
                subject = "General",
                difficulty = difficulty
            )
        )
    }

    /**
     * Valida una pregunta de quiz
     */
    fun validateQuestion(question: QuizQuestion): Boolean {
        return question.question.isNotBlank() &&
                question.options.size == 4 &&
                question.options.all { it.isNotBlank() } &&
                question.correctAnswer in 0..3 &&
                question.explanation.isNotBlank()
    }

    /**
     * Calcula estadísticas de un quiz
     */
    fun calculateQuizStats(questions: List<QuizQuestion>, userAnswers: List<Int?>): Map<String, Any> {
        val totalQuestions = questions.size
        val answeredQuestions = userAnswers.count { it != null }
        val correctAnswers = questions.mapIndexed { index, question ->
            userAnswers.getOrNull(index) == question.correctAnswer
        }.count { it }

        val percentage = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions * 100).toInt()
        } else 0

        return mapOf(
            "totalQuestions" to totalQuestions,
            "answeredQuestions" to answeredQuestions,
            "correctAnswers" to correctAnswers,
            "incorrectAnswers" to (answeredQuestions - correctAnswers),
            "unanswered" to (totalQuestions - answeredQuestions),
            "percentage" to percentage,
            "grade" to getGradeFromPercentage(percentage)
        )
    }

    /**
     * Obtiene la calificación basada en el porcentaje
     */
    fun getGradeFromPercentage(percentage: Int): String {
        return when {
            percentage >= 90 -> "A"
            percentage >= 80 -> "B"
            percentage >= 70 -> "C"
            percentage >= 60 -> "D"
            else -> "F"
        }
    }

    /**
     * Genera un mensaje motivacional basado en el rendimiento
     */
    fun getMotivationalMessage(percentage: Int): String {
        return when {
            percentage >= 90 -> "¡Excelente trabajo! 🌟 Dominas el tema perfectamente."
            percentage >= 80 -> "¡Muy bien! 👏 Tienes un buen dominio del tema."
            percentage >= 70 -> "¡Buen trabajo! 👍 Vas por el camino correcto."
            percentage >= 60 -> "Bien hecho 📚 Sigue practicando para mejorar."
            else -> "¡No te rindas! 💪 La práctica hace al maestro."
        }
    }

    /**
     * Obtiene el color asociado a una materia
     */
    fun getSubjectColor(subject: String): String {
        return when (subject.lowercase()) {
            "matemáticas", "matematicas" -> "#FF6B6B"
            "química", "quimica" -> "#4ECDC4"
            "física", "fisica" -> "#45B7D1"
            "biología", "biologia" -> "#96CEB4"
            "historia" -> "#FECA57"
            "literatura" -> "#FF9FF3"
            "inglés", "ingles" -> "#54A0FF"
            "filosofía", "filosofia" -> "#5F27CD"
            "economía", "economia" -> "#00D2D3"
            "programación", "programacion" -> "#FF9F43"
            else -> "#6C7CE7"
        }
    }

    /**
     * Obtiene el icono asociado a una materia
     */
    fun getSubjectIcon(subject: String): String {
        return when (subject.lowercase()) {
            "matemáticas", "matematicas" -> "📐"
            "química", "quimica" -> "🧪"
            "física", "fisica" -> "⚛️"
            "biología", "biologia" -> "🧬"
            "historia" -> "📚"
            "literatura" -> "📖"
            "inglés", "ingles" -> "🇬🇧"
            "filosofía", "filosofia" -> "🤔"
            "economía", "economia" -> "💰"
            "programación", "programacion" -> "💻"
            "psicología", "psicologia" -> "🧠"
            "sociología", "sociologia" -> "👥"
            "geografia", "geografía" -> "🌍"
            "arte" -> "🎨"
            "música", "musica" -> "🎵"
            "derecho" -> "⚖️"
            "medicina" -> "⚕️"
            "ingeniería", "ingenieria" -> "🔧"
            "arquitectura" -> "🏛️"
            "administración", "administracion" -> "📊"
            else -> "📋"
        }
    }

    /**
     * Mezla las opciones de respuesta manteniendo la respuesta correcta
     */
    fun shuffleOptions(question: QuizQuestion): QuizQuestion {
        val optionsWithIndex = question.options.mapIndexed { index, option -> index to option }
        val shuffledOptionsWithIndex = optionsWithIndex.shuffled()
        val newCorrectAnswer = shuffledOptionsWithIndex.indexOfFirst { it.first == question.correctAnswer }

        return question.copy(
            options = shuffledOptionsWithIndex.map { it.second },
            correctAnswer = newCorrectAnswer
        )
    }
}
