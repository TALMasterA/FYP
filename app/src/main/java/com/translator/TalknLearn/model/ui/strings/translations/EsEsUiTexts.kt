package com.translator.TalknLearn.model.ui

/**
 * Spanish (es-ES) UI text map — Interfaz en español.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val EsEsUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Usar micrófono",
    UiTextKey.CopyButton to "Copiar",
    UiTextKey.SpeakScriptButton to "Leer texto original",
    UiTextKey.TranslateButton to "Traducir",
    UiTextKey.CopyTranslationButton to "Copiar traducción",
    UiTextKey.SpeakTranslationButton to "Leer traducción",
    UiTextKey.RecognizingStatus to "Grabando...Hable ahora, parada automática.",
    UiTextKey.TranslatingStatus to "Traduciendo...",
    UiTextKey.SpeakingOriginalStatus to "Leyendo original...",
    UiTextKey.SpeakingTranslationStatus to "Leyendo traducción...",
    UiTextKey.SpeakingLabel to "Leyendo",
    UiTextKey.FinishedSpeakingOriginal to "Original leído",
    UiTextKey.FinishedSpeakingTranslation to "Traducción leída",
    UiTextKey.TtsErrorTemplate to "Error de voz: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Idioma de la app",
    UiTextKey.DetectLanguageLabel to "Detectar idioma",
    UiTextKey.TranslateToLabel to "Traducir a",

    // Language names
    UiTextKey.LangEnUs to "Inglés",
    UiTextKey.LangZhHk to "Cantonés",
    UiTextKey.LangJaJp to "Japonés",
    UiTextKey.LangZhCn to "Chino (simplificado)",
    UiTextKey.LangFrFr to "Francés",
    UiTextKey.LangDeDe to "Alemán",
    UiTextKey.LangKoKr to "Coreano",
    UiTextKey.LangEsEs to "Español",
    UiTextKey.LangIdId to "Indonesio",
    UiTextKey.LangViVn to "Vietnamita",
    UiTextKey.LangThTh to "Tailandés",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Malayo",
    UiTextKey.LangPtBr to "Portugués",
    UiTextKey.LangItIt to "Italiano",
    UiTextKey.LangRuRu to "Ruso",

    // Navigation
    UiTextKey.NavHistory to "Historial",
    UiTextKey.NavLogin to "Iniciar sesión",
    UiTextKey.NavLogout to "Cerrar sesión",
    UiTextKey.NavBack to "Atrás",
    UiTextKey.ActionCancel to "Cancelar",
    UiTextKey.ActionDelete to "Eliminar",
    UiTextKey.ActionOpen to "Abrir",
    UiTextKey.ActionName to "Nombrar",
    UiTextKey.ActionSave to "Guardar",
    UiTextKey.ActionConfirm to "Confirmar",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Escriba aquí o use el micrófono...",
    UiTextKey.SpeechTranslatedPlaceholder to "La traducción aparecerá aquí...",
    UiTextKey.StatusAzureErrorTemplate to "Error de Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Error de traducción: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Inicie sesión para traducir",
    UiTextKey.StatusRecognizePreparing to "Preparando micrófono...(no hable)",
    UiTextKey.StatusRecognizeListening to "Escuchando...Hable ahora.",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Página anterior",
    UiTextKey.PaginationNextLabel to "Página siguiente",
    UiTextKey.PaginationPageLabelTemplate to "Página {page} de {total}",

    // Toast
    UiTextKey.ToastCopied to "Copiado",
    UiTextKey.DisableText to "Inicie sesión para traducir y guardar el historial.",

    // Error
    UiTextKey.ErrorRetryButton to "Reintentar",
    UiTextKey.ErrorGenericMessage to "Ocurrió un error. Inténtelo de nuevo.",

    // Shop
    UiTextKey.ShopTitle to "Tienda",
    UiTextKey.ShopCoinBalance to "Mis monedas",
    UiTextKey.ShopHistoryExpansionTitle to "Expansión de historial",
    UiTextKey.ShopHistoryExpansionDesc to "Amplíe su límite de historial para ver más traducciones.",
    UiTextKey.ShopCurrentLimit to "Límite actual: {limit} entradas",
    UiTextKey.ShopMaxLimit to "Límite máximo:",
    UiTextKey.ShopBuyHistoryExpansion to "Comprar (+{increment} entradas, {cost} monedas)",
    UiTextKey.ShopInsufficientCoins to "Monedas insuficientes",
    UiTextKey.ShopMaxLimitReached to "Límite máximo alcanzado",
    UiTextKey.ShopHistoryExpandedTitle to "¡Expandido con éxito!",
    UiTextKey.ShopHistoryExpandedMessage to "¡Su límite de historial es ahora {limit} entradas! ¡Puede ver más traducciones!",
    UiTextKey.ShopColorPaletteTitle to "Temas de color",
    UiTextKey.ShopColorPaletteDesc to "Elija su tema de color, 10 monedas cada uno",
    UiTextKey.ShopEntry to "Tienda",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Configuración de voz",
    UiTextKey.VoiceSettingsDesc to "Elija la voz de lectura para cada idioma.",

    // Instructions
    UiTextKey.SpeechInstructions to "Toque el micrófono para reconocimiento de voz, luego traducir. Si la detección automática no se actualiza después de cambiar texto o idioma, toque actualizar arriba a la derecha.",
    UiTextKey.HomeInstructions to "Seleccione una función para comenzar.",
    UiTextKey.ContinuousInstructions to "Seleccione dos idiomas e inicie el modo conversación.",

    // Home
    UiTextKey.HomeTitle to "Traducción instantánea",
    UiTextKey.HelpTitle to "Ayuda",
    UiTextKey.SpeechTitle to "Traducción rápida",
    UiTextKey.HomeStartButton to "Iniciar traducción",
    UiTextKey.HomeFeaturesTitle to "Funciones",
    UiTextKey.HomeDiscreteDescription to "Traducción breve de texto y voz",
    UiTextKey.HomeContinuousDescription to "Traducción bidireccional en tiempo real",
    UiTextKey.HomeLearningDescription to "Generar material de aprendizaje y cuestionarios del historial",

    // Help
    UiTextKey.HelpCurrentTitle to "Funciones actuales",
    UiTextKey.HelpCautionTitle to "Precauciones",
    UiTextKey.HelpCurrentFeatures to "Funciones actuales:\n" +
            "  • Traducción rápida: reconocimiento de voz y traducción\n" +
            "  • Conversación en vivo: traducción de voz bidireccional\n" +
            "  • Historial: ver traducciones anteriores\n" +
            "  • Material de aprendizaje: generar vocabulario y cuestionarios\n\n" +
            "Traducción:\n" +
            "  • Reconocimiento de voz Azure AI\n" +
            "  • Servicio de traducción Azure\n",
    UiTextKey.HelpCaution to "Precauciones:\n" +
            "  • El reconocimiento de voz requiere conexión a internet\n" +
            "  • La caché de traducción local está disponible sin conexión\n" +
            "  • Verifique traducciones importantes con un profesional\n\n" +
            "Cuenta y datos:\n" +
            "  • Historial, aprendizaje y monedas requieren inicio de sesión\n" +
            "  • Los datos se almacenan de forma segura en Firebase Firestore\n\n" +
            "Solución de problemas:\n" +
            "  • Si nada funciona, reinicie la aplicación\n",
    UiTextKey.HelpNotesTitle to "Consejos",
    UiTextKey.HelpNotes to "💡 Consejos de uso:\n\n" +
            "Para mejores traducciones:\n" +
            "  • Hable claramente y a velocidad moderada\n" +
            "  • Reduzca el ruido ambiental para mejor reconocimiento\n" +
            "  • La traducción rápida es ideal para frases cortas\n\n" +
            "Idioma de la app:\n" +
            "  • Idioma predeterminado: inglés, más idiomas por IA\n" +
            "  • La versión cantonesa está traducida manualmente\n" +
            "Actualizaciones y comentarios:\n" +
            "  • Versión de la app en Configuración → Acerca de\n" +
            "  • Enviar comentarios en Configuración → Comentarios\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Comentarios",
    UiTextKey.FeedbackDesc to "¡Gracias por sus comentarios! Comparta sugerencias, errores o valoraciones.",
    UiTextKey.FeedbackMessagePlaceholder to "Escriba sus comentarios...",
    UiTextKey.FeedbackSubmitButton to "Enviar",
    UiTextKey.FeedbackSubmitting to "Enviando...",
    UiTextKey.FeedbackSuccessTitle to "¡Gracias!",
    UiTextKey.FeedbackSuccessMessage to "Sus comentarios fueron enviados exitosamente. ¡Gracias!",
    UiTextKey.FeedbackErrorTitle to "Error al enviar",
    UiTextKey.FeedbackErrorMessage to "Error al enviar. Verifique su conexión e inténtelo de nuevo.",
    UiTextKey.FeedbackMessageRequired to "Por favor escriba sus comentarios.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Conversación en vivo",
    UiTextKey.ContinuousStartButton to "Iniciar conversación",
    UiTextKey.ContinuousStopButton to "Detener grabación",
    UiTextKey.ContinuousStartScreenButton to "Conversación en vivo",
    UiTextKey.ContinuousPersonALabel to "A habla",
    UiTextKey.ContinuousPersonBLabel to "B habla",
    UiTextKey.ContinuousCurrentStringLabel to "Texto actual:",
    UiTextKey.ContinuousSpeakerAName to "Persona A",
    UiTextKey.ContinuousSpeakerBName to "Persona B",
    UiTextKey.ContinuousTranslationSuffix to " · Traducción",
    UiTextKey.ContinuousPreparingMicText to "Preparando micrófono...(no hable)",
    UiTextKey.ContinuousTranslatingText to "Traduciendo...",

    // History
    UiTextKey.HistoryTitle to "Historial",
    UiTextKey.HistoryTabDiscrete to "Traducción rápida",
    UiTextKey.HistoryTabContinuous to "Conversación en vivo",
    UiTextKey.HistoryNoContinuousSessions to "No hay sesiones de conversación.",
    UiTextKey.HistoryNoDiscreteRecords to "No hay registros de traducción.",
    UiTextKey.DialogDeleteRecordTitle to "¿Eliminar registro?",
    UiTextKey.DialogDeleteRecordMessage to "Esta acción no se puede deshacer.",
    UiTextKey.DialogDeleteSessionTitle to "¿Eliminar conversación?",
    UiTextKey.DialogDeleteSessionMessage to "Se eliminarán todos los registros de esta conversación. No se puede deshacer.",
    UiTextKey.HistoryDeleteSessionButton to "Eliminar",
    UiTextKey.HistoryNameSessionTitle to "Nombrar",
    UiTextKey.HistorySessionNameLabel to "Nombre de conversación",
    UiTextKey.HistorySessionTitleTemplate to "Conversación {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} entradas",

    // Filter
    UiTextKey.FilterDropdownDefault to "Todos los idiomas",
    UiTextKey.FilterTitle to "Filtrar historial",
    UiTextKey.FilterLangDrop to "Idioma",
    UiTextKey.FilterKeyword to "Palabra clave",
    UiTextKey.FilterApply to "Aplicar",
    UiTextKey.FilterCancel to "Cancelar",
    UiTextKey.FilterClear to "Limpiar",
    UiTextKey.FilterHistoryScreenTitle to "Filtro",

    // Auth
    UiTextKey.AuthLoginTitle to "Iniciar sesión",
    UiTextKey.AuthRegisterTitle to "Registrarse (pausado)",
    UiTextKey.AuthLoginHint to "Use su correo y contraseña registrados.",
    UiTextKey.AuthRegisterRules to "El registro está pausado durante el desarrollo.\nNota: Un correo inexistente impedirá restablecer la contraseña.\n" +
            "Reglas de registro:\n" +
            "• Formato de correo válido (ej. nombre@ejemplo.com)\n" +
            "• Contraseña de al menos 8 caracteres\n" +
            "• La confirmación debe coincidir",
    UiTextKey.AuthEmailLabel to "Correo electrónico",
    UiTextKey.AuthPasswordLabel to "Contraseña",
    UiTextKey.AuthConfirmPasswordLabel to "Confirmar contraseña",
    UiTextKey.AuthLoginButton to "Iniciar sesión",
    UiTextKey.AuthRegisterButton to "Registrarse",
    UiTextKey.AuthToggleToRegister to "¿No tiene cuenta? Registrarse (pausado)",
    UiTextKey.AuthToggleToLogin to "¿Ya tiene cuenta? Iniciar sesión",
    UiTextKey.AuthErrorPasswordsMismatch to "Las contraseñas no coinciden.",
    UiTextKey.AuthErrorPasswordTooShort to "La contraseña debe tener al menos 8 caracteres.",
    UiTextKey.AuthRegistrationDisabled to "El registro está pausado durante el desarrollo.",
    UiTextKey.AuthResetEmailSent to "Correo de restablecimiento enviado (si existe). Revise su bandeja.",

    // Password reset
    UiTextKey.ForgotPwText to "¿Olvidó su contraseña?",
    UiTextKey.ResetPwTitle to "Restablecer contraseña",
    UiTextKey.ResetPwText to "Ingrese el correo de su cuenta. Le enviaremos un enlace de restablecimiento.\nAsegúrese de que sea el correo registrado.\n",
    UiTextKey.ResetSendingText to "Enviando...",
    UiTextKey.ResetSendText to "Enviar correo de restablecimiento",

    // Settings
    UiTextKey.SettingsTitle to "Configuración",
    UiTextKey.SettingsPrimaryLanguageTitle to "Idioma principal",
    UiTextKey.SettingsPrimaryLanguageDesc to "Se usa para explicaciones y sugerencias de aprendizaje",
    UiTextKey.SettingsPrimaryLanguageLabel to "Idioma principal",
    UiTextKey.SettingsFontSizeTitle to "Tamaño de fuente",
    UiTextKey.SettingsFontSizeDesc to "Ajuste el tamaño del texto para mejor lectura (sincronizado entre dispositivos)",
    UiTextKey.SettingsScaleTemplate to "Tamaño: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Tema de color",
    UiTextKey.SettingsColorPaletteDesc to "Elija su tema de color, 10 monedas cada uno",
    UiTextKey.SettingsColorCostTemplate to "{cost} monedas",
    UiTextKey.SettingsColorUnlockButton to "Desbloquear",
    UiTextKey.SettingsColorSelectButton to "Seleccionar",
    UiTextKey.SettingsColorAlreadyUnlocked to "Desbloqueado",
    UiTextKey.SettingsPreviewHeadline to "Título: Vista previa de texto grande",
    UiTextKey.SettingsPreviewBody to "Cuerpo: Vista previa de texto normal",
    UiTextKey.SettingsPreviewLabel to "Etiqueta: Vista previa de texto pequeño",
    UiTextKey.SettingsAboutTitle to "Acerca de",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Sesión iniciada, la configuración se guarda y sincroniza automáticamente.",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Elija el aspecto: Sistema, Claro, Oscuro o Programado.",
    UiTextKey.SettingsThemeSystem to "Sistema",
    UiTextKey.SettingsThemeLight to "Claro",
    UiTextKey.SettingsThemeDark to "Oscuro",
    UiTextKey.SettingsThemeScheduled to "Programado",
    UiTextKey.SettingsResetPW to "Restablecer contraseña",
    UiTextKey.SettingsQuickLinks to "Configuración detallada",
    UiTextKey.SettingsNotLoggedInWarning to "Inicie sesión para ver la configuración de la cuenta. El idioma de la app puede cambiarse sin iniciar sesión.",
    UiTextKey.SettingsVoiceTitle to "Configuración de voz",
    UiTextKey.SettingsVoiceDesc to "Elija la voz de lectura para cada idioma.",
    UiTextKey.SettingsVoiceLanguageLabel to "Idioma",
    UiTextKey.SettingsVoiceSelectLabel to "Voz",
    UiTextKey.SettingsVoiceDefault to "Predeterminada",

    // Learning
    UiTextKey.LearningTitle to "Aprendizaje",
    UiTextKey.LearningHintCount to "(*) Conteo = traducciones con este idioma.",
    UiTextKey.LearningErrorTemplate to "Error: %s",
    UiTextKey.LearningGenerate to "Generar",
    UiTextKey.LearningRegenerate to "Regenerar",
    UiTextKey.LearningGenerating to "Generando...",
    UiTextKey.LearningOpenSheetTemplate to "Hoja de {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Hoja de aprendizaje {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Idioma principal: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Registros actuales: {nowCount} (al crear: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Sin contenido en la hoja.",
    UiTextKey.LearningSheetRegenerate to "Regenerar",
    UiTextKey.LearningSheetGenerating to "Generando...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 ¿Qué es esto?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Hoja de aprendizaje generada de su historial de traducciones. Contiene vocabulario, definiciones, ejemplos y notas gramaticales. ¡Pruebe su conocimiento con el cuestionario!",
    UiTextKey.LearningRegenBlockedTitle to "No se puede regenerar ahora",
    UiTextKey.LearningRegenBlockedMessage to "Se necesitan al menos 5 registros adicionales para regenerar. Faltan {needed}.",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Faltan {needed} registros para regenerar (mín. 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ El conteo debe superar al de la última generación",
    UiTextKey.LearningRegenInfoTitle to "Reglas de regeneración",
    UiTextKey.LearningRegenInfoMessage to "Regenerar material de aprendizaje:\n\n• Primera generación: en cualquier momento\n• Regeneración: al menos 5 registros adicionales\n\nEl botón se vuelve azul cuando hay suficientes. Si está gris, ¡siga traduciendo!\n\n💡 Consejo: Si el conteo no se actualiza, reinicie la app.",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Ya se generó un cuestionario para esta versión. Genere una nueva hoja para un nuevo cuestionario.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Cuestionario: {language}",
    UiTextKey.QuizOpenButton to "📝 Cuestionario",
    UiTextKey.QuizGenerateButton to "🔄 Generar cuestionario",
    UiTextKey.QuizGenerating to "⏳ Generando...",
    UiTextKey.QuizUpToDate to "✓ Actualizado",
    UiTextKey.QuizBlocked to "🚫 Bloqueado",
    UiTextKey.QuizWait to "⏳ Espere...",
    UiTextKey.QuizMaterialsQuizTemplate to "Material: {materials} | Cuestionario: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 ¡Puede ganar monedas!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 {count} más para monedas",
    UiTextKey.QuizCancelButton to "Cancelar",
    UiTextKey.QuizPreviousButton to "Pregunta anterior",
    UiTextKey.QuizNextButton to "Siguiente pregunta",
    UiTextKey.QuizSubmitButton to "Enviar",
    UiTextKey.QuizRetakeButton to "Repetir",
    UiTextKey.QuizBackButton to "Atrás",
    UiTextKey.QuizLoadingText to "Cargando cuestionario...",
    UiTextKey.QuizGeneratingText to "Generando cuestionario...",
    UiTextKey.QuizNoMaterialsTitle to "Material no encontrado",
    UiTextKey.QuizNoMaterialsMessage to "Genere primero el material de aprendizaje, luego acceda al cuestionario.",
    UiTextKey.QuizErrorTitle to "⚠️ Error del cuestionario",
    UiTextKey.QuizErrorSuggestion to "Sugerencia: Use el botón de arriba para generar un cuestionario.",
    UiTextKey.QuizCompletedTitle to "¡Cuestionario completado!",
    UiTextKey.QuizAnswerReviewTitle to "Revisar respuestas",
    UiTextKey.QuizYourAnswerTemplate to "Su respuesta: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Respuesta correcta: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Pregunta {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ No se puede regenerar: Material({materials}) < Cuestionario({quiz}), añada más traducciones.",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Otra generación en progreso. Espere.",
    UiTextKey.QuizCoinRulesTitle to "🪙 Reglas de monedas",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Cómo ganar:",
    UiTextKey.QuizCoinRulesRequirements to "Requisitos:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Estado actual:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ ¡Puede ganar monedas en el próximo cuestionario!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• {count} más para monedas",
    UiTextKey.QuizCoinRule1Coin to "• 1 moneda por respuesta correcta",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Solo el primer intento cuenta",
    UiTextKey.QuizCoinRuleMatchMaterials to "• El cuestionario debe coincidir con la versión del material",
    UiTextKey.QuizCoinRulePlus10 to "• Al menos 10 registros más que el último cuestionario pagado",
    UiTextKey.QuizCoinRuleNoDelete to "• Las monedas no se recuperan al eliminar registros",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Material: {count} registros",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Cuestionario: {count} registros",
    UiTextKey.QuizCoinRuleGotIt to "¡Entendido!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 ¿Generar nuevo cuestionario?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ ¡Puede ganar monedas con este cuestionario! (solo primer intento)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ No puede ganar monedas con este cuestionario.",
    UiTextKey.QuizRegenNeedMoreTemplate to "Necesita {count} traducciones más para ser elegible (10 más que el último cuestionario pagado).",
    UiTextKey.QuizRegenReminder to "Consejo: Puede practicar y repetir, pero las monedas solo se otorgan en el primer intento con suficientes registros.",
    UiTextKey.QuizRegenGenerateButton to "Generar",
    UiTextKey.QuizCoinsEarnedTitle to "✨ ¡Monedas ganadas!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "¡Felicidades! ¡Ganó {Coins} monedas!",
    UiTextKey.QuizCoinsRule1 to "• 1 moneda por respuesta correcta en el primer intento",
    UiTextKey.QuizCoinsRule2 to "• Sin monedas en intentos posteriores",
    UiTextKey.QuizCoinsRule3 to "• Nuevos cuestionarios requieren 10 registros adicionales",
    UiTextKey.QuizCoinsRule4 to "• El cuestionario debe coincidir con la versión del material",
    UiTextKey.QuizCoinsRule5 to "• Monedas totales visibles en el historial",
    UiTextKey.QuizCoinsGreatButton to "¡Genial!",
    UiTextKey.QuizOutdatedMessage to "Este cuestionario está basado en una hoja anterior.",
    UiTextKey.QuizRecordsLabel to "Registros",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Mis monedas",
    UiTextKey.HistoryCoinRulesTitle to "Reglas de monedas:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Cómo ganar:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 moneda por respuesta correcta",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Solo el primer intento por versión",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• El cuestionario debe coincidir con el material actual",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Reglas anti-trampa:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Al menos 10 nuevas traducciones desde la última ganancia",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• La versión del cuestionario debe coincidir con el material",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Eliminar registros bloquea la regeneración (a menos que el conteo supere el anterior)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Sin monedas en intentos posteriores",
    UiTextKey.HistoryCoinTipsTitle to "💡 Consejos:",
    UiTextKey.HistoryCoinTipsRule1 to "• Añada traducciones regularmente",
    UiTextKey.HistoryCoinTipsRule2 to "• ¡Estudie bien antes del primer intento!",
    UiTextKey.HistoryCoinGotItButton to "¡Entendido!",

    // History info
    UiTextKey.HistoryInfoTitle to "Info del historial",
    UiTextKey.HistoryInfoLimitMessage to "El historial muestra las últimas {limit} entradas. ¡Amplíe el límite en la tienda!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Los registros más antiguos se conservan pero se ocultan por rendimiento.",
    UiTextKey.HistoryInfoFavoritesMessage to "Para guardar una traducción permanentemente, toque ❤️ como favorito en el historial.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Vea lo guardado en Configuración → Favoritos.",
    UiTextKey.HistoryInfoFilterMessage to "Use el filtro para buscar en las {limit} entradas mostradas.",
    UiTextKey.HistoryInfoGotItButton to "Entendido",

    // Word bank
    UiTextKey.WordBankTitle to "Banco de palabras",
    UiTextKey.WordBankSelectLanguage to "Seleccione un idioma para ver o crear su banco de palabras:",
    UiTextKey.WordBankNoHistory to "Sin historial de traducción",
    UiTextKey.WordBankNoHistoryHint to "¡Comience a traducir para construir su banco de palabras!",
    UiTextKey.WordBankWordsCount to "Palabras",
    UiTextKey.WordBankGenerating to "Generando...",
    UiTextKey.WordBankGenerate to "Generar banco de palabras",
    UiTextKey.WordBankRegenerate to "Regenerar banco de palabras",
    UiTextKey.WordBankRefresh to "🔄 Actualizar banco",
    UiTextKey.WordBankEmpty to "Banco de palabras vacío",
    UiTextKey.WordBankEmptyHint to "Toque arriba para generar de su historial.",
    UiTextKey.WordBankExample to "Ejemplo:",
    UiTextKey.WordBankDifficulty to "Dificultad:",
    UiTextKey.WordBankFilterCategory to "Categoría",
    UiTextKey.WordBankFilterCategoryAll to "Todas las categorías",
    UiTextKey.WordBankFilterDifficultyLabel to "Dificultad:",
    UiTextKey.WordBankFilterNoResults to "Ninguna palabra coincide con los filtros",
    UiTextKey.WordBankRefreshAvailable to "✅ ¡Actualización disponible!",
    UiTextKey.WordBankRecordsNeeded to "Registros (20 necesarios para actualizar)",
    UiTextKey.WordBankRegenInfoTitle to "Reglas de actualización",
    UiTextKey.WordBankRegenInfoMessage to "Actualizar banco de palabras:\n\n• Primera generación: en cualquier momento\n• Actualización: al menos 20 registros adicionales\n\nEl botón se vuelve azul cuando hay suficientes. Si está gris, ¡siga traduciendo!\n\n💡 Consejo: Si el conteo no se actualiza, reinicie la app.",
    UiTextKey.WordBankHistoryCountTemplate to "Registros actuales: {nowCount} (al crear: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "¿Cerrar sesión?",
    UiTextKey.DialogLogoutMessage to "La traducción y el acceso al historial requieren volver a iniciar sesión.",
    UiTextKey.DialogGenerateOverwriteTitle to "¿Sobrescribir material?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "El material existente será sobrescrito.\n¿Generar material para {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Perfil",
    UiTextKey.ProfileUsernameLabel to "Nombre de usuario",
    UiTextKey.ProfileUsernameHint to "Ingrese nombre de usuario",
    UiTextKey.ProfileUpdateButton to "Actualizar perfil",
    UiTextKey.ProfileUpdateSuccess to "Perfil actualizado",
    UiTextKey.ProfileUpdateError to "Error al actualizar",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Eliminar cuenta",
    UiTextKey.AccountDeleteWarning to "⚠️ ¡Esta acción es permanente e irreversible!",
    UiTextKey.AccountDeleteConfirmMessage to "Se eliminarán permanentemente todos los datos: historial, banco de palabras, material de aprendizaje, configuración. Ingrese su contraseña para confirmar.",
    UiTextKey.AccountDeletePasswordLabel to "Contraseña",
    UiTextKey.AccountDeleteButton to "Eliminar cuenta",
    UiTextKey.AccountDeleteSuccess to "Cuenta eliminada exitosamente",
    UiTextKey.AccountDeleteError to "Error al eliminar",
    UiTextKey.AccountDeleteReauthRequired to "Ingrese su contraseña para confirmar la eliminación",

    // Favorites
    UiTextKey.FavoritesTitle to "Favoritos",
    UiTextKey.FavoritesEmpty to "Sin favoritos",
    UiTextKey.FavoritesAddSuccess to "Añadido a favoritos",
    UiTextKey.FavoritesRemoveSuccess to "Eliminado de favoritos",
    UiTextKey.FavoritesAddButton to "Añadir a favoritos",
    UiTextKey.FavoritesRemoveButton to "Eliminar de favoritos",
    UiTextKey.FavoritesNoteLabel to "Nota",
    UiTextKey.FavoritesNoteHint to "Añadir nota (opcional)",
    UiTextKey.FavoritesTabRecords to "Registros",
    UiTextKey.FavoritesTabSessions to "Conversaciones",
    UiTextKey.FavoritesSessionsEmpty to "Sin conversaciones guardadas",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} mensajes",

    // Custom words
    UiTextKey.CustomWordsTitle to "Palabras personalizadas",
    UiTextKey.CustomWordsAdd to "Añadir palabra",
    UiTextKey.CustomWordsEdit to "Editar palabra",
    UiTextKey.CustomWordsDelete to "Eliminar palabra",
    UiTextKey.CustomWordsOriginalLabel to "Palabra original",
    UiTextKey.CustomWordsTranslatedLabel to "Traducción",
    UiTextKey.CustomWordsPronunciationLabel to "Pronunciación (opcional)",
    UiTextKey.CustomWordsExampleLabel to "Ejemplo (opcional)",
    UiTextKey.CustomWordsSaveSuccess to "Palabra guardada",
    UiTextKey.CustomWordsDeleteSuccess to "Palabra eliminada",
    UiTextKey.CustomWordsAlreadyExists to "Esta palabra ya existe",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Idioma original",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Idioma de traducción",
    UiTextKey.CustomWordsSaveButton to "Guardar",
    UiTextKey.CustomWordsCancelButton to "Cancelar",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Auto-detección",
    UiTextKey.LanguageDetectDetecting to "Detectando...",
    UiTextKey.LanguageDetectedTemplate to "Detectado: {language}",
    UiTextKey.LanguageDetectFailed to "Detección fallida",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Escanear texto de imagen",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Aviso: El reconocimiento de texto puede no ser completamente preciso. Verifique el texto reconocido." +
            "Compatible con latino (inglés, etc.), chino, japonés y coreano.",
    UiTextKey.ImageRecognitionScanning to "Escaneando texto...",
    UiTextKey.ImageRecognitionSuccess to "Texto reconocido exitosamente",

    // Cache
    UiTextKey.CacheClearButton to "Limpiar caché",
    UiTextKey.CacheClearSuccess to "Caché limpiado",
    UiTextKey.CacheStatsTemplate to "Caché: {count} traducciones guardadas",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Tema automático",
    UiTextKey.SettingsAutoThemeDesc to "Cambiar automáticamente entre claro y oscuro según la hora",
    UiTextKey.SettingsAutoThemeEnabled to "Activado",
    UiTextKey.SettingsAutoThemeDisabled to "Desactivado",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Modo oscuro desde:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Modo claro desde:",
    UiTextKey.SettingsAutoThemePreview to "El tema cambiará automáticamente a las horas configuradas",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Modo sin conexión",
    UiTextKey.OfflineModeMessage to "Está sin conexión. Se muestran los datos almacenados.",
    UiTextKey.OfflineModeRetry to "Reintentar conexión",
    UiTextKey.OfflineDataCached to "Datos almacenados disponibles",
    UiTextKey.OfflineSyncPending to "Los cambios se sincronizarán cuando esté en línea",

    // Image capture
    UiTextKey.ImageSourceTitle to "Seleccionar fuente de imagen",
    UiTextKey.ImageSourceCamera to "Tomar foto",
    UiTextKey.ImageSourceGallery to "Elegir de galería",
    UiTextKey.ImageSourceCancel to "Cancelar",
    UiTextKey.CameraCaptureContentDesc to "Capturar",

    // Friends
    UiTextKey.FriendsTitle to "Amigos",
    UiTextKey.FriendsMenuButton to "Amigos",
    UiTextKey.FriendsAddButton to "Añadir amigo",
    UiTextKey.FriendsSearchTitle to "Buscar usuario",
    UiTextKey.FriendsSearchPlaceholder to "Nombre de usuario o ID...",
    UiTextKey.FriendsSearchMinChars to "Ingrese al menos 2 caracteres",
    UiTextKey.FriendsSearchNoResults to "No se encontraron usuarios",
    UiTextKey.FriendsListEmpty to "Añada amigos para chatear y compartir material de aprendizaje.",
    UiTextKey.FriendsRequestsSection to "Solicitudes de amistad ({count})",
    UiTextKey.FriendsSectionTitle to "Amigos ({count})",
    UiTextKey.FriendsAcceptButton to "Aceptar",
    UiTextKey.FriendsRejectButton to "Rechazar",
    UiTextKey.FriendsRemoveButton to "Eliminar",
    UiTextKey.FriendsRemoveDialogTitle to "Eliminar amigo",
    UiTextKey.FriendsRemoveDialogMessage to "¿Eliminar a {username} de su lista de amigos?",
    UiTextKey.FriendsSendRequestButton to "Añadir",
    UiTextKey.FriendsRequestSentSuccess to "¡Solicitud de amistad enviada!",
    UiTextKey.FriendsRequestAcceptedSuccess to "¡Solicitud aceptada!",
    UiTextKey.FriendsRequestRejectedSuccess to "Solicitud rechazada",
    UiTextKey.FriendsRemovedSuccess to "Amigo eliminado",
    UiTextKey.FriendsRequestFailed to "Error al enviar",
    UiTextKey.FriendsCloseButton to "Cerrar",
    UiTextKey.FriendsCancelButton to "Cancelar",
    UiTextKey.FriendsRemoveConfirm to "Eliminar",
    UiTextKey.FriendsNewRequestsTemplate to "¡Tiene {count} nueva(s) solicitud(es) de amistad!",
    UiTextKey.FriendsSentRequestsSection to "Solicitudes enviadas ({count})",
    UiTextKey.FriendsPendingStatus to "Pendiente",
    UiTextKey.FriendsCancelRequestButton to "Cancelar solicitud",
    UiTextKey.FriendsUnreadMessageDesc to "Enviar mensaje",
    UiTextKey.FriendsDeleteModeButton to "Eliminar amigos",
    UiTextKey.FriendsDeleteSelectedButton to "Eliminar seleccionados",
    UiTextKey.FriendsDeleteMultipleTitle to "Eliminar amigos",
    UiTextKey.FriendsDeleteMultipleMessage to "¿Eliminar a los {count} amigos seleccionados?",
    UiTextKey.FriendsSearchMinChars3 to "Ingrese al menos 3 caracteres para nombres",
    UiTextKey.FriendsSearchByUserIdHint to "O busque exactamente por ID de usuario",
    UiTextKey.FriendsStatusAlreadyFriends to "Ya son amigos",
    UiTextKey.FriendsStatusRequestSent to "Solicitud enviada — esperando respuesta",
    UiTextKey.FriendsStatusRequestReceived to "Este usuario le envió una solicitud",

    // Chat
    UiTextKey.ChatTitle to "Chat con {username}",
    UiTextKey.ChatInputPlaceholder to "Escriba un mensaje...",
    UiTextKey.ChatSendButton to "Enviar",
    UiTextKey.ChatEmpty to "Sin mensajes. ¡Inicie una conversación!",
    UiTextKey.ChatMessageSent to "Mensaje enviado",
    UiTextKey.ChatMessageFailed to "Error al enviar",
    UiTextKey.ChatMarkingRead to "Marcando...",
    UiTextKey.ChatLoadingMessages to "Cargando mensajes...",
    UiTextKey.ChatToday to "Hoy",
    UiTextKey.ChatYesterday to "Ayer",
    UiTextKey.ChatUnreadBadge to "{count} sin leer",
    UiTextKey.ChatTranslateButton to "Traducir",
    UiTextKey.ChatTranslateDialogTitle to "Traducir conversación",
    UiTextKey.ChatTranslateDialogMessage to "¿Traducir los mensajes de su amigo a su idioma? Se detectará y traducirá el idioma de cada mensaje.",
    UiTextKey.ChatTranslateConfirm to "Traducir todo",
    UiTextKey.ChatTranslating to "Traduciendo mensajes...",
    UiTextKey.ChatTranslated to "Mensajes traducidos",
    UiTextKey.ChatShowOriginal to "Mostrar original",
    UiTextKey.ChatShowTranslation to "Mostrar traducción",
    UiTextKey.ChatTranslateFailed to "Error en la traducción",
    UiTextKey.ChatTranslatedLabel to "Traducido",

    // Sharing
    UiTextKey.ShareTitle to "Compartir",
    UiTextKey.ShareInboxTitle to "Bandeja compartida",
    UiTextKey.ShareInboxEmpty to "Sin contenido compartido. ¡Sus amigos pueden compartir palabras y material!",
    UiTextKey.ShareWordButton to "Compartir palabra",
    UiTextKey.ShareMaterialButton to "Compartir material",
    UiTextKey.ShareSelectFriendTitle to "Seleccionar amigo",
    UiTextKey.ShareSelectFriendMessage to "Elija un amigo para compartir:",
    UiTextKey.ShareSuccess to "¡Compartido exitosamente!",
    UiTextKey.ShareFailed to "Error al compartir",
    UiTextKey.ShareWordWith to "Compartir palabra con {username}",
    UiTextKey.ShareMaterialWith to "Compartir material con {username}",
    UiTextKey.ShareAcceptButton to "Aceptar",
    UiTextKey.ShareDismissButton to "Descartar",
    UiTextKey.ShareAccepted to "Añadido a su colección",
    UiTextKey.ShareDismissed to "Elemento descartado",
    UiTextKey.ShareActionFailed to "Error en la acción",
    UiTextKey.ShareTypeWord to "Palabra",
    UiTextKey.ShareTypeLearningSheet to "Hoja de aprendizaje",
    UiTextKey.ShareReceivedFrom to "De: {username}",
    UiTextKey.ShareNewItemsTemplate to "¡{count} elemento(s) nuevo(s)!",
    UiTextKey.ShareViewFullMaterial to "Toque \"Ver\" para el material completo",
    UiTextKey.ShareDeleteItemTitle to "Eliminar elemento",
    UiTextKey.ShareDeleteItemMessage to "¿Eliminar este elemento compartido? No se puede deshacer.",
    UiTextKey.ShareDeleteButton to "Eliminar",
    UiTextKey.ShareViewButton to "Ver",
    UiTextKey.ShareItemNotFound to "Elemento no encontrado.",
    UiTextKey.ShareNoContent to "Sin contenido en este material.",
    UiTextKey.ShareSaveToSelf to "Guardar en mi caja",
    UiTextKey.ShareSavedToSelf to "¡Guardado en su caja!",

    // My profile
    UiTextKey.MyProfileTitle to "Mi perfil",
    UiTextKey.MyProfileUserId to "ID de usuario",
    UiTextKey.MyProfileUsername to "Nombre de usuario",
    UiTextKey.MyProfileDisplayName to "Nombre visible",
    UiTextKey.MyProfileCopyUserId to "Copiar ID",
    UiTextKey.MyProfileCopyUsername to "Copiar nombre",
    UiTextKey.MyProfileShare to "Compartir perfil",
    UiTextKey.MyProfileCopied to "¡Copiado al portapapeles!",
    UiTextKey.MyProfileLanguages to "Idiomas",
    UiTextKey.MyProfilePrimaryLanguage to "Idioma principal",
    UiTextKey.MyProfileLearningLanguages to "Idiomas de aprendizaje",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Página de amigos",
    UiTextKey.FriendsInfoMessage to "• Deslice hacia abajo para actualizar lista, solicitudes y estado.\n" +
            "• Toque una tarjeta para abrir el chat.\n" +
            "• El punto rojo (●) indica mensajes no leídos, ✓✓ para marcar leído.\n" +
            "• 📥 para la bandeja compartida, ✓✓ para limpiar el punto.\n" +
            "• 🚫 para bloquear — el amigo es eliminado y no podrá contactarle.\n" +
            "• Bloquear también elimina el historial de chat.\n" +
            "• Icono de papelera para modo eliminación.\n" +
            "• Eliminar un amigo también borra todos los mensajes.\n" +
            "• Icono de búsqueda para buscar por nombre o ID.\n" +
            "• Las notificaciones push están desactivadas por defecto — actívelas en configuración.\n",
    UiTextKey.FriendsEmptyTitle to "Sin amigos",
    UiTextKey.FriendsEmptyMessage to "Toque \"Añadir amigo\" para buscar por nombre o ID.\n",
    UiTextKey.FriendsInfoGotItButton to "Entendido",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Página de aprendizaje",
    UiTextKey.LearningInfoMessage to "• Deslice para actualizar los registros.\n" +
            "• Cada tarjeta muestra idioma y conteo.\n" +
            "• \"Generar\" para una hoja (1ª vez gratis).\n" +
            "• La regeneración requiere mín. 5 registros adicionales.\n" +
            "• El botón de hoja abre el material generado.\n" +
            "• Después de la hoja puede hacer el cuestionario.",
    UiTextKey.LearningEmptyTitle to "Sin historial de traducción",
    UiTextKey.LearningEmptyMessage to "Comience a traducir para crear registros.\n" +
            "Las hojas se generan del historial.\n" +
            "Después de traducir, deslice para actualizar.",
    UiTextKey.LearningInfoGotItButton to "Entendido",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Página del banco de palabras",
    UiTextKey.WordBankInfoMessage to "• Deslice para actualizar la lista de idiomas.\n" +
            "• Seleccione un idioma para ver o crear.\n" +
            "• El banco se genera del historial.\n" +
            "• La actualización requiere mín. 20 registros adicionales.\n" +
            "• Añada palabras personalizadas manualmente.\n" +
            "• Comparta palabras con amigos.",
    UiTextKey.WordBankInfoGotItButton to "Entendido",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Bandeja compartida",
    UiTextKey.ShareInboxInfoMessage to "• Deslice para actualizar la bandeja.\n" +
            "• Los elementos compartidos por amigos aparecen aquí.\n" +
            "• Acepte o descarte palabras.\n" +
            "• \"Ver\" para hojas y cuestionarios.\n" +
            "• El punto rojo (●) indica elementos nuevos/no leídos.\n" +
            "• Confirmación antes de descartar una palabra compartida.",
    UiTextKey.ShareInboxInfoGotItButton to "Entendido",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibilidad del perfil",
    UiTextKey.MyProfileVisibilityPublic to "Público",
    UiTextKey.MyProfileVisibilityPrivate to "Privado",
    UiTextKey.MyProfileVisibilityDescription to "Público: Cualquiera puede encontrarle y añadirle.\nPrivado: No aparece en búsquedas.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Descartar palabra",
    UiTextKey.ShareDismissWordMessage to "¿Descartar esta palabra compartida? No se puede deshacer.",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Idioma: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Cerrar",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Conectado o pendiente",
    UiTextKey.AccessibilityNewMessages to "Nuevos mensajes",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicador de nuevos elementos",
    UiTextKey.AccessibilitySuccessIcon to "Éxito",
    UiTextKey.AccessibilityErrorIcon to "Error",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Tipo de elemento compartido",
    UiTextKey.AccessibilityAddCustomWords to "Añadir palabras personalizadas",
    UiTextKey.AccessibilityWordBankExists to "Banco de palabras existe",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.Comentarios",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Config. notif.",
    UiTextKey.FriendsNotifSettingsTitle to "Configuración de notificaciones",
    UiTextKey.FriendsNotifNewMessages to "Nuevos mensajes de chat",
    UiTextKey.FriendsNotifFriendRequests to "Solicitudes de amistad recibidas",
    UiTextKey.FriendsNotifRequestAccepted to "Solicitud de amistad aceptada",
    UiTextKey.FriendsNotifSharedInbox to "Nuevos elementos compartidos",
    UiTextKey.FriendsNotifCloseButton to "Listo",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Insignias en la app (puntos rojos)",
    UiTextKey.InAppBadgeMessages to "Insignia de mensajes no leídos",
    UiTextKey.InAppBadgeFriendRequests to "Insignia de solicitudes de amistad",
    UiTextKey.InAppBadgeSharedInbox to "Insignia de bandeja compartida no leída",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Inicie sesión para continuar.",
    UiTextKey.ErrorSaveFailedRetry to "Error al guardar. Inténtelo de nuevo.",
    UiTextKey.ErrorLoadFailedRetry to "Error al cargar. Inténtelo de nuevo.",
    UiTextKey.ErrorNetworkRetry to "Error de red. Verifique su conexión.",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Faltan {needed} traducciones para crear material",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Cambiar a conversación en vivo →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Limpiar chat",
    UiTextKey.ChatClearConversationTitle to "Limpiar conversación",
    UiTextKey.ChatClearConversationMessage to "¿Ocultar todos los mensajes de esta conversación? Permanecerán ocultos al volver a abrir. La otra persona no se ve afectada.",
    UiTextKey.ChatClearConversationConfirm to "Limpiar todo",
    UiTextKey.ChatClearConversationSuccess to "Conversación limpiada",

    // Block user
    UiTextKey.BlockUserButton to "Bloquear",
    UiTextKey.BlockUserTitle to "¿Bloquear a este usuario?",
    UiTextKey.BlockUserMessage to "¿Bloquear a {username}? Será eliminado de su lista y no podrá contactarle.",
    UiTextKey.BlockUserConfirm to "Bloquear",
    UiTextKey.BlockUserSuccess to "Usuario bloqueado y eliminado de la lista.",
    UiTextKey.BlockedUsersTitle to "Usuarios bloqueados",
    UiTextKey.BlockedUsersEmpty to "Sin usuarios bloqueados.",
    UiTextKey.UnblockUserButton to "Desbloquear",
    UiTextKey.UnblockUserTitle to "¿Desbloquear?",
    UiTextKey.UnblockUserMessage to "¿Desbloquear a {username}? Podrá enviarle solicitudes de nuevo.",
    UiTextKey.UnblockUserSuccess to "Usuario desbloqueado.",
    UiTextKey.BlockedUsersManageButton to "Gestionar bloqueados",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Nota de solicitud (opcional)",
    UiTextKey.FriendsRequestNotePlaceholder to "Añadir nota breve...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "¡Hoja lista! Toque para abrir.",
    UiTextKey.GenerationBannerWordBank to "¡Banco de palabras listo! Toque para ver.",
    UiTextKey.GenerationBannerQuiz to "¡Cuestionario listo! Toque para iniciar.",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Notificaciones",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Chino (tradicional)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistema de amigos",
    UiTextKey.HelpFriendSystemBody to "• Buscar amigos por nombre o ID\n" +
            "• Enviar, aceptar o rechazar solicitudes\n" +
            "• Chat en tiempo real con traducción\n" +
            "• Compartir palabras y material de aprendizaje\n" +
            "• Gestionar contenido compartido en la bandeja\n" +
            "• El punto rojo (●) indica contenido no leído o nuevo\n" +
            "• Deslice para actualizar",
    UiTextKey.HelpProfileVisibilityTitle to "Visibilidad del perfil",
    UiTextKey.HelpProfileVisibilityBody to "• Perfil público o privado en configuración\n" +
            "• Público: Cualquiera puede encontrarle\n" +
            "• Privado: Invisible en búsquedas\n" +
            "• Aún privado: Comparta su ID para ser añadido",
    UiTextKey.HelpColorPalettesTitle to "Temas y monedas",
    UiTextKey.HelpColorPalettesBody to "• 1 tema gratis: Azul cielo (predeterminado)\n" +
            "• 10 temas desbloqueables, 10 monedas cada uno\n" +
            "• Gane monedas con cuestionarios\n" +
            "• Monedas para temas y expansión de historial\n" +
            "• Tema automático: Claro 6–18h, Oscuro 18–6h",
    UiTextKey.HelpPrivacyTitle to "Privacidad y datos",
    UiTextKey.HelpPrivacyBody to "• Audio solo para reconocimiento, sin almacenamiento permanente\n" +
            "• OCR procesado en el dispositivo (privacidad)\n" +
            "• Cuenta y datos eliminables en cualquier momento\n" +
            "• Modo privado: no visible en búsquedas\n" +
            "• Todos los datos se sincronizan de forma segura vía Firebase",
    UiTextKey.HelpAppVersionTitle to "Versión de la app",
    UiTextKey.HelpAppVersionNotes to "• Límite de historial: 30 a 60 entradas (ampliable con monedas)\n" +
            "• El nombre de usuario es único — cambiarlo libera el anterior\n" +
            "• Cierre de sesión automático en actualizaciones de seguridad\n" +
            "• Todas las traducciones por Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Traducción instantánea",
    UiTextKey.OnboardingPage1Desc to "Traducción rápida para frases cortas, conversación en vivo para diálogos bidireccionales.",
    UiTextKey.OnboardingPage2Title to "Aprende vocabulario",
    UiTextKey.OnboardingPage2Desc to "Genere hojas de vocabulario y cuestionarios de su historial.",
    UiTextKey.OnboardingPage3Title to "Conéctese con amigos",
    UiTextKey.OnboardingPage3Desc to "Chatee, comparta palabras y aprenda juntos.",
    UiTextKey.OnboardingSkipButton to "Omitir",
    UiTextKey.OnboardingNextButton to "Siguiente",
    UiTextKey.OnboardingGetStartedButton to "Comenzar",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 ¡Bienvenido de vuelta, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Usuario:",
    UiTextKey.ChatUserIdLabel to "ID de usuario:",
    UiTextKey.ChatLearningLabel to "Aprendiendo:",
    UiTextKey.ChatBlockedMessage to "No puede enviar mensajes a este usuario.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Buscar",
    UiTextKey.CustomWordsEmptyState to "Sin palabras personalizadas",
    UiTextKey.CustomWordsEmptyHint to "Toque + para añadir una palabra",
    UiTextKey.CustomWordsNoSearchResults to "Sin palabras coincidentes",
    UiTextKey.AddCustomWordHintTemplate to "Ingrese la palabra en {from} y la traducción en {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} registros",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Correo: {email}",
    UiTextKey.ProfileUsernameHintFull to "Nombre de usuario para amigos (3–20 caracteres, alfanuméricos/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Sin opciones de voz para este idioma",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "La app fue actualizada. Inicie sesión de nuevo",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Límite de favoritos alcanzado",
    UiTextKey.FavoritesLimitMessage to "Máximo 20 favoritos. Elimine uno para añadir otro.",
    UiTextKey.FavoritesLimitGotIt to "Entendido",
    UiTextKey.FavoritesInfoTitle to "Info de favoritos",
    UiTextKey.FavoritesInfoMessage to "Máximo 20 favoritos (registros y conversaciones). Este límite reduce la carga de la base de datos. Elimine uno para añadir otro.",
    UiTextKey.FavoritesInfoGotIt to "Entendido",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Cambio no disponible",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "El idioma principal solo puede cambiarse cada 30 días. Espere {days} día(s) más.",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "El idioma principal solo puede cambiarse cada 30 días. Espere {hours} hora(s) más.",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Confirmar cambio de idioma",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Cambiar el idioma principal impedirá cambiarlo durante 30 días. ¿Continuar?",

    // Bottom navigation
    UiTextKey.NavHome to "Inicio",
    UiTextKey.NavTranslate to "Traducir",
    UiTextKey.NavLearn to "Aprender",
    UiTextKey.NavFriends to "Amigos",
    UiTextKey.NavSettings to "Configuración",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Permiso de cámara requerido",
    UiTextKey.CameraPermissionMessage to "Permita el acceso a la cámara para el reconocimiento de texto.",
    UiTextKey.CameraPermissionGrant to "Permitir",
    UiTextKey.MicPermissionMessage to "Se necesita acceso al micrófono para el reconocimiento de voz.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "¿Eliminar los {count} elementos seleccionados? No se puede deshacer.",
    UiTextKey.WordBankDeleteConfirm to "¿Eliminar \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Aceptar todas",
    UiTextKey.FriendsRejectAllButton to "Rechazar todas",
    UiTextKey.ChatBlockedCannotSend to "No se puede enviar mensaje",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "¿Desbloquear {name}?",
    UiTextKey.ShopUnlockCost to "Costo: {cost} monedas",
    UiTextKey.ShopYourCoins to "Mis monedas: {coins}",
    UiTextKey.ShopUnlockButton to "Desbloquear",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Idioma principal",
    UiTextKey.HelpPrimaryLanguageBody to "• El idioma principal se usa para explicaciones de aprendizaje\n" +
            "• Solo cambiable cada 30 días para consistencia\n" +
            "• Cambiable en configuración\n" +
            "• Configuración global para todas las páginas",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Consejo: Para mejor reconocimiento, configure \"Idioma fuente\" al idioma del texto a escanear.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Cambio no disponible",
    UiTextKey.SettingsUsernameCooldownMessage to "El nombre de usuario solo puede cambiarse cada 30 días. Espere {days} día(s) más.",
    UiTextKey.SettingsUsernameCooldownMessageHours to "El nombre de usuario solo puede cambiarse cada 30 días. Espere {hours} hora(s) más.",
    UiTextKey.SettingsUsernameConfirmTitle to "Confirmar cambio de nombre",
    UiTextKey.SettingsUsernameConfirmMessage to "Cambiar el nombre de usuario impedirá cambiarlo durante 30 días. ¿Continuar?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Sin conexión a internet. Verifique su conexión.",
    UiTextKey.ErrorPermissionDenied to "No tiene permiso para esta acción.",
    UiTextKey.ErrorSessionExpired to "Sesión expirada. Inicie sesión de nuevo.",
    UiTextKey.ErrorItemNotFound to "Elemento no encontrado. Puede haber sido eliminado.",
    UiTextKey.ErrorAccessDenied to "Acceso denegado.",
    UiTextKey.ErrorAlreadyFriends to "Ya son amigos con este usuario.",
    UiTextKey.ErrorUserBlocked to "Acción no permitida. El usuario puede estar bloqueado.",
    UiTextKey.ErrorRequestNotFound to "Esta solicitud ya no existe.",
    UiTextKey.ErrorRequestAlreadyHandled to "Esta solicitud ya fue procesada.",
    UiTextKey.ErrorNotAuthorized to "No está autorizado para realizar esta acción.",
    UiTextKey.ErrorRateLimited to "Demasiadas solicitudes. Inténtelo más tarde.",
    UiTextKey.ErrorInvalidInput to "Entrada inválida. Verifique e inténtelo de nuevo.",
    UiTextKey.ErrorOperationNotAllowed to "Esta operación no está permitida actualmente.",
    UiTextKey.ErrorTimeout to "Tiempo de espera agotado. Inténtelo de nuevo.",
    UiTextKey.ErrorSendMessageFailed to "Error al enviar mensaje. Inténtelo de nuevo.",
    UiTextKey.ErrorFriendRequestSent to "¡Solicitud de amistad enviada!",
    UiTextKey.ErrorFriendRequestFailed to "Error al enviar solicitud.",
    UiTextKey.ErrorFriendRemoved to "Amigo eliminado.",
    UiTextKey.ErrorFriendRemoveFailed to "Error al eliminar. Verifique su conexión.",
    UiTextKey.ErrorBlockSuccess to "Usuario bloqueado.",
    UiTextKey.ErrorBlockFailed to "Error al bloquear. Inténtelo de nuevo.",
    UiTextKey.ErrorUnblockSuccess to "Usuario desbloqueado.",
    UiTextKey.ErrorUnblockFailed to "Error al desbloquear. Inténtelo de nuevo.",
    UiTextKey.ErrorAcceptRequestSuccess to "¡Solicitud de amistad aceptada!",
    UiTextKey.ErrorAcceptRequestFailed to "Error al aceptar. Inténtelo de nuevo.",
    UiTextKey.ErrorRejectRequestSuccess to "Solicitud de amistad rechazada.",
    UiTextKey.ErrorRejectRequestFailed to "Error al rechazar. Inténtelo de nuevo.",
    UiTextKey.ErrorOfflineMessage to "Está sin conexión. Algunas funciones pueden no estar disponibles.",
    UiTextKey.ErrorChatDeletionFailed to "Error al eliminar chat. Inténtelo de nuevo.",
    UiTextKey.ErrorGenericRetry to "Ocurrió un error. Inténtelo de nuevo.",
)
