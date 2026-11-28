package com.translator.TalknLearn.model.ui

/**
 * Italian (it-IT) UI text map — Testi dell'interfaccia in italiano.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val ItItUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Usa microfono",
    UiTextKey.CopyButton to "Copia",
    UiTextKey.SpeakScriptButton to "Leggi originale",
    UiTextKey.TranslateButton to "Traduci",
    UiTextKey.CopyTranslationButton to "Copia traduzione",
    UiTextKey.SpeakTranslationButton to "Leggi traduzione",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Lingua dell'app",
    UiTextKey.DetectLanguageLabel to "Rileva lingua",
    UiTextKey.TranslateToLabel to "Traduci in",

    // Language names
    UiTextKey.LangEnUs to "Inglese",
    UiTextKey.LangZhHk to "Cantonese",
    UiTextKey.LangJaJp to "Giapponese",
    UiTextKey.LangZhCn to "Cinese (Semplificato)",
    UiTextKey.LangFrFr to "Francese",
    UiTextKey.LangDeDe to "Tedesco",
    UiTextKey.LangKoKr to "Coreano",
    UiTextKey.LangEsEs to "Spagnolo",
    UiTextKey.LangIdId to "Indonesiano",
    UiTextKey.LangViVn to "Vietnamita",
    UiTextKey.LangThTh to "Tailandese",
    UiTextKey.LangFilPh to "Filippino",
    UiTextKey.LangMsMy to "Malese",
    UiTextKey.LangPtBr to "Portoghese",
    UiTextKey.LangItIt to "Italiano",
    UiTextKey.LangRuRu to "Russo",

    // Navigation
    UiTextKey.NavHistory to "Cronologia",
    UiTextKey.NavLogin to "Accedi",
    UiTextKey.NavLogout to "Esci",
    UiTextKey.NavBack to "Indietro",
    UiTextKey.ActionCancel to "Annulla",
    UiTextKey.ActionDelete to "Elimina",
    UiTextKey.ActionOpen to "Apri",
    UiTextKey.ActionName to "Rinomina",
    UiTextKey.ActionSave to "Salva",
    UiTextKey.ActionConfirm to "Conferma",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Digita qui o usa il microfono...",
    UiTextKey.SpeechTranslatedPlaceholder to "La traduzione apparirà qui...",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Precedente",
    UiTextKey.PaginationNextLabel to "Successivo",
    UiTextKey.PaginationPageLabelTemplate to "Pagina {page} / {total}",

    // Toast
    UiTextKey.DisableText to "Accedi per tradurre e salvare la cronologia",

    // Error
    UiTextKey.ErrorRetryButton to "Riprova",

    // Shop
    UiTextKey.ShopTitle to "Negozio",
    UiTextKey.ShopCoinBalance to "Le mie monete",
    UiTextKey.ShopHistoryExpansionTitle to "Espandi cronologia",
    UiTextKey.ShopHistoryExpansionDesc to "Aumenta il limite della cronologia per vedere più traduzioni",
    UiTextKey.ShopCurrentLimit to "Limite attuale: {limit} voci",
    UiTextKey.ShopMaxLimit to "Limite massimo:",
    UiTextKey.ShopBuyHistoryExpansion to "Acquista (+{increment} voci, {cost} monete)",
    UiTextKey.ShopInsufficientCoins to "Monete insufficienti",
    UiTextKey.ShopMaxLimitReached to "Limite massimo raggiunto",
    UiTextKey.ShopHistoryExpandedTitle to "Espansione riuscita!",
    UiTextKey.ShopHistoryExpandedMessage to "La tua cronologia è ora di {limit} voci! Puoi vedere più traduzioni!",
    UiTextKey.ShopColorPaletteTitle to "Temi colore",
    UiTextKey.ShopColorPaletteDesc to "Scegli un tema colore. 10 monete ciascuno",
    UiTextKey.ShopEntry to "Negozio",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Impostazioni voce",
    UiTextKey.VoiceSettingsDesc to "Scegli la voce per ogni lingua",

    // Instructions
    UiTextKey.SpeechInstructions to "Tocca il microfono per il riconoscimento vocale. Se il rilevamento automatico non si aggiorna dopo il cambio, tocca aggiorna in alto a destra",
    UiTextKey.HomeInstructions to "Seleziona una funzione per iniziare",
    UiTextKey.ContinuousInstructions to "Scegli due lingue e inizia la conversazione",

    // Home
    UiTextKey.HomeTitle to "Traduzione istantanea",
    UiTextKey.HelpTitle to "Guida",
    UiTextKey.SpeechTitle to "Traduzione rapida",
    UiTextKey.HomeStartButton to "Inizia a tradurre",

    // Help
    UiTextKey.HelpCurrentTitle to "Funzionalità attuali",
    UiTextKey.HelpCautionTitle to "Avvertenze",
    UiTextKey.HelpCurrentFeatures to "Funzionalità attuali:\n" +
            "  • Traduzione rapida: riconoscimento e traduzione vocale\n" +
            "  • Conversazione continua: traduzione vocale bidirezionale\n" +
            "  • Cronologia: visualizza traduzioni passate\n" +
            "  • Materiali di apprendimento: crea vocabolario e quiz\n\n" +
            "Traduzione:\n" +
            "  • Riconoscimento vocale Azure AI\n" +
            "  • Servizio di traduzione Azure\n",
    UiTextKey.HelpCaution to "Avvertenze:\n" +
            "  • Il riconoscimento vocale richiede internet\n" +
            "  • Le traduzioni in cache funzionano offline\n" +
            "  • Verifica le traduzioni importanti con esperti\n\n" +
            "Account e dati:\n" +
            "  • Accesso richiesto per cronologia, apprendimento e monete\n" +
            "  • Dati archiviati in sicurezza su Firebase Firestore\n\n" +
            "Risoluzione problemi:\n" +
            "  • Se non funziona, riavvia l'app\n",
    UiTextKey.HelpNotesTitle to "Suggerimenti",
    UiTextKey.HelpNotes to "💡 Suggerimenti d'uso:\n\n" +
            "Per migliori traduzioni:\n" +
            "  • Parla chiaramente a velocità normale\n" +
            "  • Riduci il rumore per miglior riconoscimento\n" +
            "  • Traduzione rapida per frasi brevi\n\n" +
            "Lingua dell'app:\n" +
            "  • Predefinita: inglese, altre tramite IA\n" +
            "  • Traduzione manuale per cantonese\n" +
            "Aggiornamenti e feedback:\n" +
            "  • Versione app in Impostazioni → Info\n" +
            "  • Invia feedback in Impostazioni → Feedback\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Feedback",
    UiTextKey.FeedbackDesc to "Grazie! Condividi suggerimenti, bug o valutazioni",
    UiTextKey.FeedbackMessagePlaceholder to "Scrivi il tuo feedback...",
    UiTextKey.FeedbackSubmitButton to "Invia",
    UiTextKey.FeedbackSubmitting to "Invio in corso...",
    UiTextKey.FeedbackSuccessTitle to "Grazie!",
    UiTextKey.FeedbackSuccessMessage to "Feedback inviato con successo. Grazie!",
    UiTextKey.FeedbackErrorTitle to "Invio fallito",
    UiTextKey.FeedbackErrorMessage to "Invio fallito. Controlla la connessione e riprova",
    UiTextKey.FeedbackMessageRequired to "Messaggio di feedback obbligatorio",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Conversazione continua",
    UiTextKey.ContinuousStartButton to "Avvia conversazione",
    UiTextKey.ContinuousStopButton to "Ferma registrazione",
    UiTextKey.ContinuousStartScreenButton to "Conversazione continua",
    UiTextKey.ContinuousPersonALabel to "Parlante A",
    UiTextKey.ContinuousPersonBLabel to "Parlante B",
    UiTextKey.ContinuousCurrentStringLabel to "Testo corrente:",
    UiTextKey.ContinuousSpeakerAName to "Persona A",
    UiTextKey.ContinuousSpeakerBName to "Persona B",
    UiTextKey.ContinuousTranslationSuffix to " · Traduzione",
    UiTextKey.ContinuousPreparingMicText to "Preparazione microfono...(non parlare ancora)",
    UiTextKey.ContinuousTranslatingText to "Traducendo...",

    // History
    UiTextKey.HistoryTitle to "Cronologia",
    UiTextKey.HistoryTabDiscrete to "Traduzione rapida",
    UiTextKey.HistoryTabContinuous to "Conversazione continua",
    UiTextKey.HistoryNoContinuousSessions to "Nessuna sessione di conversazione",
    UiTextKey.HistoryNoDiscreteRecords to "Nessuna traduzione ancora",
    UiTextKey.DialogDeleteRecordTitle to "Eliminare voce?",
    UiTextKey.DialogDeleteRecordMessage to "Questa azione non può essere annullata",
    UiTextKey.DialogDeleteSessionTitle to "Eliminare sessione?",
    UiTextKey.DialogDeleteSessionMessage to "Tutte le voci in questa sessione saranno eliminate. Non è reversibile",
    UiTextKey.HistoryDeleteSessionButton to "Elimina",
    UiTextKey.HistoryNameSessionTitle to "Rinomina",
    UiTextKey.HistorySessionNameLabel to "Nome sessione",
    UiTextKey.HistorySessionTitleTemplate to "Sessione {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} voci",

    // Filter
    UiTextKey.FilterDropdownDefault to "Tutte le lingue",
    UiTextKey.FilterTitle to "Filtra cronologia",
    UiTextKey.FilterLangDrop to "Lingua",
    UiTextKey.FilterKeyword to "Parola chiave",
    UiTextKey.FilterApply to "Applica",
    UiTextKey.FilterCancel to "Annulla",
    UiTextKey.FilterClear to "Cancella filtro",
    UiTextKey.FilterHistoryScreenTitle to "Filtro",

    // Auth
    UiTextKey.AuthLoginTitle to "Accedi",
    UiTextKey.AuthRegisterTitle to "Registrati (Sospeso)",
    UiTextKey.AuthLoginHint to "Usa email e password registrati",
    UiTextKey.AuthRegisterRules to "Registrazione sospesa durante lo sviluppo\nAttenzione: La password non può essere recuperata se l'email è sbagliata\n" +
            "Regole di registrazione:\n" +
            "• Formato email valido (es: nome@esempio.com)\n" +
            "• Password di almeno 8 caratteri\n" +
            "• Conferma password deve corrispondere",
    UiTextKey.AuthEmailLabel to "Email",
    UiTextKey.AuthPasswordLabel to "Password",
    UiTextKey.AuthConfirmPasswordLabel to "Conferma password",
        UiTextKey.AuthLoginButton to "Accedi",
        UiTextKey.AuthGoogleSignInButton to "Accedi con Google",
    UiTextKey.AuthRegisterButton to "Registrati",
    UiTextKey.AuthToggleToRegister to "Non hai un account? Registrati (Sospeso)",
    UiTextKey.AuthToggleToLogin to "Hai già un account? Accedi",
    UiTextKey.AuthErrorPasswordsMismatch to "Le password non corrispondono",
    UiTextKey.AuthErrorPasswordTooShort to "La password deve avere almeno 8 caratteri",
    UiTextKey.AuthRegistrationDisabled to "Registrazione sospesa durante lo sviluppo",
    UiTextKey.AuthResetEmailSent to "Email di reimpostazione inviata (se l'account esiste). Controlla la posta",

    // Password reset
    UiTextKey.ForgotPwText to "Password dimenticata?",
    UiTextKey.ResetPwTitle to "Reimposta password",
    UiTextKey.ResetPwText to "Inserisci l'email dell'account. Ti invieremo un link di reimpostazione\nAssicurati che l'email sia registrata\n",
    UiTextKey.ResetSendingText to "Invio in corso...",
    UiTextKey.ResetSendText to "Invia email di reimpostazione",

    // Settings
    UiTextKey.SettingsTitle to "Impostazioni",
    UiTextKey.SettingsPrimaryLanguageTitle to "Lingua principale",
    UiTextKey.SettingsPrimaryLanguageDesc to "Usata per descrizioni e guide di apprendimento",
    UiTextKey.SettingsPrimaryLanguageLabel to "Lingua principale",
    UiTextKey.SettingsFontSizeTitle to "Dimensione carattere",
    UiTextKey.SettingsFontSizeDesc to "Regola la dimensione del carattere (sincronizzata tra dispositivi)",
    UiTextKey.SettingsScaleTemplate to "Scala: {pct}%",
    UiTextKey.SettingsColorCostTemplate to "{cost} monete",
    UiTextKey.SettingsColorSelectButton to "Seleziona",
    UiTextKey.SettingsColorAlreadyUnlocked to "Già sbloccato",
    UiTextKey.SettingsPreviewHeadline to "Titolo: Testo grande",
    UiTextKey.SettingsPreviewBody to "Corpo: Testo normale",
    UiTextKey.SettingsPreviewLabel to "Etichetta: Testo piccolo",
    UiTextKey.SettingsAboutTitle to "Info",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Connesso. Impostazioni salvate e sincronizzate automaticamente",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Scegli il tema: Sistema, Chiaro, Scuro o Programmato",
    UiTextKey.SettingsThemeSystem to "Sistema",
    UiTextKey.SettingsThemeLight to "Chiaro",
    UiTextKey.SettingsThemeDark to "Scuro",
    UiTextKey.SettingsThemeScheduled to "Programmato",
    UiTextKey.SettingsResetPW to "Reimposta password",
    UiTextKey.SettingsQuickLinks to "Altre impostazioni",
    UiTextKey.SettingsNotLoggedInWarning to "Accedi per vedere le impostazioni account. La lingua dell'app può essere cambiata senza accesso",
    UiTextKey.SettingsVoiceLanguageLabel to "Lingua",
    UiTextKey.SettingsVoiceSelectLabel to "Voce",
    UiTextKey.SettingsVoiceDefault to "Predefinita",

    // Learning
    UiTextKey.LearningTitle to "Apprendimento",
    UiTextKey.LearningHintCount to "(*) Conteggio = traduzioni che coinvolgono questa lingua",
    UiTextKey.LearningErrorTemplate to "Errore: %s",
    UiTextKey.LearningGenerate to "Genera",
    UiTextKey.LearningRegenerate to "Rigenera",
    UiTextKey.LearningGenerating to "Generazione...",
    UiTextKey.LearningOpenSheetTemplate to "Materiale {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Materiale di apprendimento {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Lingua principale: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Conteggio attuale: {nowCount} (alla generazione: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Nessun contenuto",
    UiTextKey.LearningSheetRegenerate to "Rigenera",
    UiTextKey.LearningSheetGenerating to "Generazione...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Cos'è questo?",
    UiTextKey.LearningSheetWhatIsThisDesc to "I materiali di apprendimento sono generati dalla cronologia delle traduzioni. Includono vocabolario, definizioni, esempi e note grammaticali. Metti alla prova le tue conoscenze con i quiz!",
    UiTextKey.LearningRegenBlockedTitle to "Impossibile rigenerare",
    UiTextKey.LearningRegenBlockedMessage to "Servono almeno 5 voci aggiuntive per rigenerare. Ne servono ancora {needed}",
    UiTextKey.LearningRegenInfoTitle to "Regole di rigenerazione",
    UiTextKey.LearningRegenInfoMessage to "Rigenerare materiali:\n\n• Prima volta: in qualsiasi momento\n• Rigenerare: servono 5 voci in più\n\nIl pulsante diventerà verde quando pronto. Se grigio, fai più traduzioni!\n\n💡 Suggerimento: Se il conteggio non si aggiorna, riavvia l'app",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz: {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Genera quiz",
    UiTextKey.QuizGenerating to "⏳ Generazione...",
    UiTextKey.QuizBlocked to "🚫 Bloccato",
    UiTextKey.QuizWait to "⏳ Attendi...",
    UiTextKey.QuizCanEarnCoins to "🪙 Puoi guadagnare monete!",
    UiTextKey.QuizCancelButton to "Annulla",
    UiTextKey.QuizPreviousButton to "Precedente",
    UiTextKey.QuizNextButton to "Successivo",
    UiTextKey.QuizSubmitButton to "Invia",
    UiTextKey.QuizRetakeButton to "Ripeti",
    UiTextKey.QuizBackButton to "Indietro",
    UiTextKey.QuizLoadingText to "Caricamento quiz...",
    UiTextKey.QuizGeneratingText to "Generazione quiz...",
    UiTextKey.QuizNoMaterialsTitle to "Nessun materiale trovato",
    UiTextKey.QuizNoMaterialsMessage to "Genera prima i materiali di apprendimento prima di provare il quiz",
    UiTextKey.QuizErrorTitle to "⚠️ Errore quiz",
    UiTextKey.QuizErrorSuggestion to "Suggerimento: Usa il pulsante sopra per generare il quiz",
    UiTextKey.QuizCompletedTitle to "Quiz completato!",
    UiTextKey.QuizAnswerReviewTitle to "Revisione risposte",
    UiTextKey.QuizYourAnswerTemplate to "La tua risposta: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Risposta corretta: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Domanda {current} / {total}",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Un'altra generazione in corso. Attendi",
    UiTextKey.QuizCoinRulesTitle to "🪙 Regole monete",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Come guadagnare:",
    UiTextKey.QuizCoinRulesRequirements to "Requisiti:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Stato attuale:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Puoi guadagnare monete al prossimo quiz!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Servono ancora {count} per le monete",
    UiTextKey.QuizCoinRule1Coin to "• 1 moneta per risposta corretta",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Solo il primo tentativo conta",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Il quiz deve corrispondere ai materiali",
    UiTextKey.QuizCoinRulePlus10 to "• Almeno 10 voci aggiuntive dall'ultimo premio",
    UiTextKey.QuizCoinRuleNoDelete to "• Le monete non vengono restituite se le voci vengono eliminate",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Materiale: {count} voci",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Quiz: {count} voci",
    UiTextKey.QuizCoinRuleGotIt to "Ho capito!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Rigenerare quiz?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Puoi guadagnare monete in questo quiz! (solo primo tentativo)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Non puoi guadagnare monete in questo quiz",
    UiTextKey.QuizRegenNeedMoreTemplate to "Servono ancora {count} traduzioni per qualificarsi (più 10 dall'ultimo premio)",
    UiTextKey.QuizRegenReminder to "Suggerimento: Puoi esercitarti e ripetere, ma le monete solo al primo tentativo con voci sufficienti",
    UiTextKey.QuizRegenGenerateButton to "Genera",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Monete guadagnate!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Complimenti! Hai guadagnato {Coins} monete!",
    UiTextKey.QuizCoinsRule1 to "• 1 moneta per risposta corretta al primo tentativo",
    UiTextKey.QuizCoinsRule2 to "• Nessuna moneta nei tentativi successivi",
    UiTextKey.QuizCoinsRule3 to "• Nuovo quiz richiede più 10 voci",
    UiTextKey.QuizCoinsRule4 to "• Il quiz deve corrispondere ai materiali",
    UiTextKey.QuizCoinsRule5 to "• Le monete fanno parte della cronologia",
    UiTextKey.QuizCoinsGreatButton to "Fantastico!",
    UiTextKey.QuizRecordsLabel to "Voci",

    // History coins

    // History info
    UiTextKey.HistoryInfoTitle to "Info cronologia",
    UiTextKey.HistoryInfoLimitMessage to "La cronologia mostra le {limit} voci più recenti. Espandi nel negozio!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Le voci più vecchie vengono conservate ma nascoste per le prestazioni",
    UiTextKey.HistoryInfoFavoritesMessage to "Se vuoi conservare una traduzione, tocca ❤️ per salvarla nei preferiti",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Visualizza i salvati in Impostazioni → Preferiti",
    UiTextKey.HistoryInfoFilterMessage to "Usa il filtro per cercare nelle {limit} voci visualizzate",
    UiTextKey.HistoryInfoGotItButton to "Ho capito",

    // Word bank
    UiTextKey.WordBankTitle to "Banca parole",
    UiTextKey.WordBankSelectLanguage to "Seleziona una lingua per visualizzare o generare la banca parole:",
    UiTextKey.WordBankNoHistory to "Nessuna cronologia di traduzione",
    UiTextKey.WordBankNoHistoryHint to "Inizia a tradurre per creare la tua banca parole!",
    UiTextKey.WordBankWordsCount to "Parole",
    UiTextKey.WordBankGenerating to "Generazione...",
    UiTextKey.WordBankGenerate to "Genera banca parole",
    UiTextKey.WordBankRefresh to "🔄 Aggiorna banca",
    UiTextKey.WordBankEmpty to "Banca parole vuota",
    UiTextKey.WordBankEmptyHint to "Tocca sopra per generare dalla cronologia",
    UiTextKey.WordBankExample to "Esempio:",
    UiTextKey.WordBankDifficulty to "Livello:",
    UiTextKey.WordBankFilterCategory to "Categoria",
    UiTextKey.WordBankFilterCategoryAll to "Tutte le categorie",
    UiTextKey.WordBankFilterDifficultyLabel to "Livello:",
    UiTextKey.WordBankFilterNoResults to "Nessuna parola corrisponde al filtro",
    UiTextKey.WordBankRegenInfoTitle to "Regole di aggiornamento",
    UiTextKey.WordBankRegenInfoMessage to "Aggiornare la banca parole:\n\n• Prima volta: in qualsiasi momento\n• Aggiornare: servono 20 voci in più\n\nIl pulsante diventerà verde quando pronto. Se grigio, fai più traduzioni!\n\n💡 Suggerimento: Se il conteggio non si aggiorna, riavvia l'app",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Uscire?",
    UiTextKey.DialogLogoutMessage to "Dovrai accedere di nuovo per tradurre e vedere la cronologia",
    UiTextKey.DialogGenerateOverwriteTitle to "Sostituire materiale?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Il materiale attuale verrà sostituito\nGenerare materiale per {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Profilo",
    UiTextKey.ProfileUsernameLabel to "Nome utente",
    UiTextKey.ProfileUsernameHint to "Inserisci nome utente",
    UiTextKey.ProfileUpdateButton to "Aggiorna profilo",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Elimina account",
    UiTextKey.AccountDeleteWarning to "⚠️ Permanente e irreversibile!",
    UiTextKey.AccountDeleteConfirmMessage to "Tutti i dati saranno eliminati permanentemente: cronologia, banca parole, materiali, impostazioni. Inserisci la password per confermare",
    UiTextKey.AccountDeletePasswordLabel to "Password",
        UiTextKey.AccountDeleteButton to "Elimina account e dati",

    // Favorites
    UiTextKey.FavoritesTitle to "Preferiti",
    UiTextKey.FavoritesEmpty to "Nessun preferito ancora",
    UiTextKey.FavoritesTabRecords to "Voci",
    UiTextKey.FavoritesTabSessions to "Sessioni",
    UiTextKey.FavoritesSessionsEmpty to "Nessuna sessione preferita",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} messaggi",

    // Custom words
    UiTextKey.CustomWordsTitle to "Parole personalizzate",
    UiTextKey.CustomWordsAdd to "Aggiungi parola",
    UiTextKey.CustomWordsEdit to "Modifica parola",
    UiTextKey.CustomWordsDelete to "Elimina parola",
    UiTextKey.CustomWordsOriginalLabel to "Originale",
    UiTextKey.CustomWordsTranslatedLabel to "Traduzione",
    UiTextKey.CustomWordsPronunciationLabel to "Pronuncia (opzionale)",
    UiTextKey.CustomWordsExampleLabel to "Esempio (opzionale)",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Lingua originale",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Lingua traduzione",
    UiTextKey.CustomWordsSaveButton to "Salva",
    UiTextKey.CustomWordsCancelButton to "Annulla",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Rilevamento automatico",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Scansiona testo dall'immagine",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Avviso: Il riconoscimento testo potrebbe non essere completamente preciso. Rivedi il testo rilevato. " +
            "Supporta Latino (Inglese ecc.), Cinese, Giapponese e Coreano",

    // Cache

    // Auto theme
    UiTextKey.SettingsAutoThemePreview to "Il tema cambierà automaticamente secondo l'orario impostato",

    // Offline mode
    UiTextKey.OfflineSyncPending to "Le modifiche saranno sincronizzate quando online",

    // Image capture
    UiTextKey.ImageSourceTitle to "Scegli fonte immagine",
    UiTextKey.ImageSourceCamera to "Scatta foto",
    UiTextKey.ImageSourceGallery to "Scegli dalla galleria",
    UiTextKey.ImageSourceCancel to "Annulla",
    UiTextKey.CameraCaptureContentDesc to "Scatta foto",

    // Friends
    UiTextKey.FriendsTitle to "Amici",
    UiTextKey.FriendsAddButton to "Aggiungi amico",
    UiTextKey.FriendsSearchTitle to "Cerca utente",
    UiTextKey.FriendsSearchPlaceholder to "Nome utente o ID...",
    UiTextKey.FriendsSearchNoResults to "Nessun utente trovato",
    UiTextKey.FriendsRequestsSection to "Richieste ({count})",
    UiTextKey.FriendsSectionTitle to "Amici ({count})",
    UiTextKey.FriendsAcceptButton to "Accetta",
    UiTextKey.FriendsRejectButton to "Rifiuta",
    UiTextKey.FriendsSendRequestButton to "Aggiungi",
    UiTextKey.FriendsCloseButton to "Chiudi",
    UiTextKey.FriendsCancelButton to "Annulla",
    UiTextKey.FriendsRemoveConfirm to "Rimuovi",
    UiTextKey.FriendsNewRequestsTemplate to "{count} nuove richieste!",
    UiTextKey.FriendsSentRequestsSection to "Inviate ({count})",
    UiTextKey.FriendsPendingStatus to "In attesa",
    UiTextKey.FriendsCancelRequestButton to "Annulla richiesta",
    UiTextKey.FriendsUnreadMessageDesc to "Invia messaggio",
    UiTextKey.FriendsDeleteModeButton to "Elimina amici",
    UiTextKey.FriendsDeleteMultipleTitle to "Eliminare amici",
    UiTextKey.FriendsDeleteMultipleMessage to "Eliminare {count} amici selezionati?",
    UiTextKey.FriendsSearchMinChars3 to "Inserisci almeno 3 caratteri per nome",
    UiTextKey.FriendsSearchByUserIdHint to "Oppure cerca esattamente per User ID",
    UiTextKey.FriendsStatusAlreadyFriends to "Già amici",
    UiTextKey.FriendsStatusRequestSent to "Inviata — in attesa di risposta",
    UiTextKey.FriendsStatusRequestReceived to "Questo utente ti ha inviato una richiesta",

    // Chat
    UiTextKey.ChatTitle to "Chat con {username}",
    UiTextKey.ChatInputPlaceholder to "Scrivi un messaggio...",
    UiTextKey.ChatSendButton to "Invia",
    UiTextKey.ChatEmpty to "Nessun messaggio ancora. Inizia la conversazione!",
    UiTextKey.ChatUnreadBadge to "{count} non letti",
    UiTextKey.ChatTranslateButton to "Traduci",
    UiTextKey.ChatTranslateDialogTitle to "Traduci chat",
    UiTextKey.ChatTranslateDialogMessage to "Tradurre i messaggi dell'amico nella tua lingua? Ogni messaggio sarà rilevato e tradotto automaticamente",
    UiTextKey.ChatTranslateConfirm to "Traduci tutto",
    UiTextKey.ChatShowOriginal to "Mostra originale",
    UiTextKey.ChatShowTranslation to "Mostra traduzione",
    UiTextKey.ChatTranslatedLabel to "Tradotto",

    // Sharing
    UiTextKey.ShareInboxTitle to "Posta condivisioni",
    UiTextKey.ShareInboxEmpty to "Nessuna condivisione ancora. Gli amici possono condividere parole e materiali!",
    UiTextKey.ShareWordButton to "Condividi parola",
    UiTextKey.ShareMaterialButton to "Condividi materiale",
    UiTextKey.ShareSelectFriendTitle to "Seleziona amico",
    UiTextKey.ShareSelectFriendMessage to "Seleziona un amico per condividere:",
    UiTextKey.ShareSuccess to "Condiviso con successo!",
    UiTextKey.ShareAcceptButton to "Accetta",
    UiTextKey.ShareDismissButton to "Rifiuta",
    UiTextKey.ShareTypeWord to "Parola",
    UiTextKey.ShareTypeLearningSheet to "Materiale di apprendimento",
    UiTextKey.ShareReceivedFrom to "Da: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} nuovi elementi!",
    UiTextKey.ShareViewFullMaterial to "Tocca \"Vedi\" per visualizzare il materiale completo",
    UiTextKey.ShareDeleteItemTitle to "Eliminare elemento",
    UiTextKey.ShareDeleteItemMessage to "Eliminare questo elemento condiviso? Non è reversibile",
    UiTextKey.ShareDeleteButton to "Elimina",
    UiTextKey.ShareViewButton to "Vedi",
    UiTextKey.ShareItemNotFound to "Elemento non trovato",
    UiTextKey.ShareNoContent to "Nessun contenuto nel materiale",
    UiTextKey.ShareSaveToSelf to "Salva nella propria posta",
    UiTextKey.ShareSavedToSelf to "Salvato nella tua posta!",

    // My profile
    UiTextKey.MyProfileTitle to "Il mio profilo",
    UiTextKey.MyProfileUserId to "User ID",
    UiTextKey.MyProfileUsername to "Nome utente",
    UiTextKey.MyProfileCopyUserId to "Copia ID",
    UiTextKey.MyProfileCopyUsername to "Copia nome utente",
    UiTextKey.MyProfileShare to "Condividi profilo",
    UiTextKey.MyProfileCopied to "Copiato!",
    UiTextKey.MyProfilePrimaryLanguage to "Lingua principale",
    UiTextKey.MyProfileLearningLanguages to "Lingue in apprendimento",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Pagina amici",
    UiTextKey.FriendsInfoMessage to "• Scorri per aggiornare lista, richieste e stato\n" +
            "• Tocca la scheda per aprire la chat\n" +
            "• Punto rosso (●) per messaggi non letti; ✓✓ per tutti letti\n" +
            "• 📥 per posta condivisioni; ✓✓ per cancellare badge\n" +
            "• 🚫 per bloccare — rimuove l'amico e blocca le interazioni\n" +
            "• Bloccare elimina anche la cronologia chat\n" +
            "• Icona cestino per modalità eliminazione\n" +
            "• Rimuovere un amico elimina tutti i messaggi\n" +
            "• Icona cerca per cercare per nome o ID\n" +
            "• Notifiche disattivate per default — attiva in Impostazioni\n",
    UiTextKey.FriendsEmptyTitle to "Nessun amico ancora",
    UiTextKey.FriendsEmptyMessage to "Tocca \"Aggiungi amico\" per cercare per nome o ID\n",
    UiTextKey.FriendsInfoGotItButton to "Ho capito",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Pagina apprendimento",
    UiTextKey.LearningInfoMessage to "• Scorri per aggiornare la lista\n" +
            "• Ogni scheda mostra lingua e conteggio\n" +
            "• \"Genera\" per materiali (gratis la prima volta)\n" +
            "• Rigenerare richiede 5 voci aggiuntive\n" +
            "• Pulsante materiale per aprire il generato\n" +
            "• Dopo aver generato il materiale, puoi fare il quiz",
    UiTextKey.LearningEmptyMessage to "Inizia a tradurre per creare voci\n" +
            "I materiali vengono generati dalla cronologia\n" +
            "Dopo aver tradotto, scorri per aggiornare",
    UiTextKey.LearningInfoGotItButton to "Ho capito",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Pagina banca parole",
    UiTextKey.WordBankInfoMessage to "• Scorri per aggiornare la lista lingue\n" +
            "• Seleziona una lingua per visualizzare o generare\n" +
            "• Banca parole generata dalla cronologia\n" +
            "• Aggiornare richiede 20 voci aggiuntive\n" +
            "• Aggiungi parole personalizzate manualmente\n" +
            "• Condividi parole con gli amici",
    UiTextKey.WordBankInfoGotItButton to "Ho capito",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Posta condivisioni",
    UiTextKey.ShareInboxInfoMessage to "• Scorri per aggiornare la posta\n" +
            "• Le condivisioni degli amici appaiono qui\n" +
            "• Accetta o rifiuta parole\n" +
            "• \"Vedi\" per materiali e quiz\n" +
            "• Punto rosso (●) per elementi nuovi/non letti\n" +
            "• Conferma prima di rifiutare parole condivise",
    UiTextKey.ShareInboxInfoGotItButton to "Ho capito",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibilità profilo",
    UiTextKey.MyProfileVisibilityPublic to "Pubblico",
    UiTextKey.MyProfileVisibilityPrivate to "Privato",
    UiTextKey.MyProfileVisibilityDescription to "Pubblico: Chiunque può cercarti e aggiungerti\nPrivato: Non appari nelle ricerche",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Rifiutare parola",
    UiTextKey.ShareDismissWordMessage to "Rifiutare la parola condivisa? Non è reversibile",

    // Shared inbox sheet label

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Chiudi",
    UiTextKey.AccessibilityNewMessages to "Nuovi messaggi",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicatore nuovi elementi",
    UiTextKey.AccessibilitySuccessIcon to "Successo",
    UiTextKey.AccessibilityErrorIcon to "Errore",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Tipo elemento condiviso",
    UiTextKey.AccessibilityWordBankExists to "Banca parole esistente",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "IT.Feedback",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Impostazioni notifiche",
    UiTextKey.FriendsNotifSettingsTitle to "Impostazioni notifiche",
    UiTextKey.FriendsNotifNewMessages to "Nuovi messaggi chat",
    UiTextKey.FriendsNotifFriendRequests to "Richieste accettate",
    UiTextKey.FriendsNotifRequestAccepted to "Richieste approvate",
    UiTextKey.FriendsNotifSharedInbox to "Nuovi elementi condivisi",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Badge nell'app (punti rossi)",
    UiTextKey.InAppBadgeMessages to "Badge messaggi non letti",
    UiTextKey.InAppBadgeFriendRequests to "Badge richieste di amicizia",
    UiTextKey.InAppBadgeSharedInbox to "Badge posta condivisioni",

    // Error messages

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Servono ancora {needed} traduzioni per generare il materiale",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Passa alla conversazione continua →",

    // Chat clear
    UiTextKey.ChatClearConversationTitle to "Cancellare conversazione",
    UiTextKey.ChatClearConversationMessage to "Nascondere tutti i messaggi? Rimarranno nascosti alla riapertura. Non influisce sull'altra persona",
    UiTextKey.ChatClearConversationConfirm to "Cancella tutto",
    UiTextKey.ChatClearConversationSuccess to "Conversazione cancellata",

    // Block user
    UiTextKey.BlockUserButton to "Blocca",
    UiTextKey.BlockUserTitle to "Bloccare questo utente?",
    UiTextKey.BlockUserMessage to "Bloccare {username}? Verrà rimosso dalla lista e bloccato dalle interazioni",
    UiTextKey.BlockUserConfirm to "Blocca",
    UiTextKey.BlockedUsersTitle to "Utenti bloccati",
    UiTextKey.BlockedUsersEmpty to "Nessun utente bloccato",
    UiTextKey.UnblockUserButton to "Sblocca",
    UiTextKey.UnblockUserTitle to "Sbloccare?",
    UiTextKey.UnblockUserMessage to "Sbloccare {username}? Potranno inviare richieste di nuovo",
    UiTextKey.BlockedUsersManageButton to "Gestisci bloccati",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Nota richiesta (opzionale)",
    UiTextKey.FriendsRequestNotePlaceholder to "Aggiungi nota breve...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Materiale pronto! Tocca per aprire",
    UiTextKey.GenerationBannerWordBank to "Banca parole pronta! Tocca per vedere",
    UiTextKey.GenerationBannerQuiz to "Quiz pronto! Tocca per iniziare",

    // Notification settings quick link

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Cinese (Tradizionale)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistema amici",
    UiTextKey.HelpFriendSystemBody to "• Cerca amici per nome o ID\n" +
            "• Invia, accetta o rifiuta richieste\n" +
            "• Chatta direttamente con traduzione\n" +
            "• Condividi parole e materiali di apprendimento\n" +
            "• Gestisci contenuti condivisi nella posta\n" +
            "• Punto rosso (●) per contenuto nuovo/non letto\n" +
            "• Scorri verso il basso per aggiornare",
    UiTextKey.HelpProfileVisibilityTitle to "Visibilità profilo",
    UiTextKey.HelpProfileVisibilityBody to "• Imposta il profilo come pubblico o privato in Impostazioni\n" +
            "• Pubblico: Chiunque può cercarti\n" +
            "• Privato: Non appari nelle ricerche\n" +
            "• Usa privato: condividi l'ID per essere aggiunto",
    UiTextKey.HelpColorPalettesTitle to "Temi e monete",
    UiTextKey.HelpColorPalettesBody to "• 1 tema gratuito: Sky Blue (predefinito)\n" +
            "• 10 temi sbloccabili a 10 monete ciascuno\n" +
            "• Guadagna monete nei quiz\n" +
            "• Usa monete per temi e espansione cronologia\n" +
            "• Tema automatico: chiaro 6-18, scuro 18-6",
    UiTextKey.HelpPrivacyTitle to "Privacy e dati",
    UiTextKey.HelpPrivacyBody to "• Voce usata solo per riconoscimento, non archiviata permanentemente\n" +
            "• OCR elaborato sul dispositivo (sicuro)\n" +
            "• Puoi eliminare account e dati in qualsiasi momento\n" +
            "• Modalità privata: non appari nelle ricerche\n" +
            "• Tutti i dati sincronizzati in sicurezza su Firebase",
    UiTextKey.HelpAppVersionTitle to "Versione app",
    UiTextKey.HelpAppVersionNotes to "• Limite cronologia: da 30 a 60 voci (espandi con monete)\n" +
            "• Nomi utente unici — cambia per liberare il vecchio\n" +
            "• Logout automatico con aggiornamenti di sicurezza\n" +
            "• Tutte le traduzioni tramite Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Traduzione istantanea",
    UiTextKey.OnboardingPage1Desc to "Traduzione rapida per frasi brevi. Conversazione continua per discussioni bidirezionali",
    UiTextKey.OnboardingPage2Title to "Impara vocabolario",
    UiTextKey.OnboardingPage2Desc to "Crea liste di parole e quiz dalla cronologia",
    UiTextKey.OnboardingPage3Title to "Connettiti con gli amici",
    UiTextKey.OnboardingPage3Desc to "Chatta, condividi parole e impara insieme",
    UiTextKey.OnboardingSkipButton to "Salta",
    UiTextKey.OnboardingNextButton to "Avanti",
    UiTextKey.OnboardingGetStartedButton to "Inizia",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Bentornato, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Utente:",
    UiTextKey.ChatUserIdLabel to "User ID:",
    UiTextKey.ChatLearningLabel to "Studia:",
    UiTextKey.ChatBlockedMessage to "Impossibile inviare messaggi a questo utente",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Cerca",
    UiTextKey.CustomWordsEmptyState to "Nessuna parola personalizzata ancora",
    UiTextKey.CustomWordsEmptyHint to "Tocca + per aggiungere una parola",
    UiTextKey.CustomWordsNoSearchResults to "Nessuna parola trovata",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} voci",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Email: {email}",
    UiTextKey.ProfileUsernameHintFull to "Nome utente per amici (3–20 caratteri, lettere/numeri/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Nessuna opzione voce per questa lingua",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "App aggiornata. Accedi di nuovo",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Limite preferiti raggiunto",
    UiTextKey.FavoritesLimitMessage to "Massimo 20 preferiti. Elimina altri per aggiungerne",
    UiTextKey.FavoritesLimitGotIt to "Ho capito",
    UiTextKey.FavoritesInfoTitle to "Info preferiti",
    UiTextKey.FavoritesInfoMessage to "Massimo 20 preferiti (voci e sessioni). Limitato per il carico del database. Elimina per aggiungerne nuovi",
    UiTextKey.FavoritesInfoGotIt to "Ho capito",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Non può essere cambiata",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "La lingua principale può essere cambiata ogni 30 giorni. Mancano {days} giorni",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "La lingua principale può essere cambiata ogni 30 giorni. Mancano {hours} ore",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Conferma cambio lingua",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Cambiare la lingua principale non può essere annullato per 30 giorni. Continuare?",

    // Bottom navigation
    UiTextKey.NavHome to "Home",
    UiTextKey.NavTranslate to "Traduci",
    UiTextKey.NavLearn to "Impara",
    UiTextKey.NavFriends to "Amici",
    UiTextKey.NavSettings to "Impost.",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Permesso fotocamera richiesto",
    UiTextKey.CameraPermissionMessage to "Consenti l'accesso alla fotocamera per il riconoscimento testo",
    UiTextKey.CameraPermissionGrant to "Consenti",
    UiTextKey.MicPermissionMessage to "Permesso microfono richiesto per il riconoscimento vocale",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Eliminare {count} elementi selezionati? Non è reversibile",
    UiTextKey.WordBankDeleteConfirm to "Eliminare \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Accetta tutti",
    UiTextKey.FriendsRejectAllButton to "Rifiuta tutti",
    UiTextKey.ChatBlockedCannotSend to "Impossibile inviare messaggi",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Sbloccare {name}?",
    UiTextKey.ShopUnlockCost to "Costo: {cost} monete",
    UiTextKey.ShopYourCoins to "Le mie monete: {coins}",
    UiTextKey.ShopUnlockButton to "Sblocca",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Lingua principale",
    UiTextKey.HelpPrimaryLanguageBody to "• La lingua principale è usata per le descrizioni nell'apprendimento\n" +
            "• Può essere cambiata ogni 30 giorni per coerenza\n" +
            "• Cambia in Impostazioni\n" +
            "• L'impostazione è applicata su tutte le pagine",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Suggerimento: Per miglior riconoscimento, imposta \"Lingua fonte\" sulla lingua del testo scansionato",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Non può essere cambiato",
    UiTextKey.SettingsUsernameCooldownMessage to "Il nome utente può essere cambiato ogni 30 giorni. Mancano {days} giorni",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Il nome utente può essere cambiato ogni 30 giorni. Mancano {hours} ore",
    UiTextKey.SettingsUsernameConfirmTitle to "Conferma cambio nome utente",
    UiTextKey.SettingsUsernameConfirmMessage to "Cambiare il nome utente non può essere annullato per 30 giorni. Continuare?",

    // Phase 1 fixes
    UiTextKey.SettingsPasswordManagedByGoogle to "Password gestita da Google",
    UiTextKey.SpeechListeningLabel to "In ascolto… parla ora",
    UiTextKey.QuizLeaveTitle to "Uscire dal quiz?",
    UiTextKey.QuizLeaveMessage to "Le tue risposte andranno perse.",
    UiTextKey.QuizLeaveConfirm to "Esci",
    UiTextKey.QuizLeaveStay to "Resta",

    // Phase 1 part 2
    UiTextKey.A11yAddFavorite to "Aggiungi ai preferiti",
    UiTextKey.A11yRemoveFavorite to "Rimuovi dai preferiti",
    UiTextKey.A11yViewFriendProfile to "Vedi profilo dell'amico",
    UiTextKey.A11yRefreshAutoDetect to "Ripeti rilevamento automatico",
    UiTextKey.A11yInstructions to "Istruzioni",
    UiTextKey.A11yClearText to "Cancella testo",
    UiTextKey.A11yAddCustomWords to "Aggiungi parole personalizzate",
    UiTextKey.A11yFilter to "Filtra",
    UiTextKey.A11ySpeakOriginal to "Leggi originale",
    UiTextKey.A11ySpeakTranslation to "Leggi traduzione",
    UiTextKey.A11ySpeakExample to "Leggi esempio",
    UiTextKey.A11yCoinRules to "Regole delle monete",
    UiTextKey.StatusWaiting to "In attesa…",
    UiTextKey.ErrorNotLoggedIn to "Accesso non effettuato",
    UiTextKey.HistoryErrorDeleteFailed to "Eliminazione non riuscita. Riprova.",
    UiTextKey.HistoryErrorRenameFailed to "Rinomina non riuscita. Riprova.",
    UiTextKey.HistoryErrorDeleteSessionFailed to "Impossibile eliminare la sessione. Riprova.",
    UiTextKey.HistoryErrorLoadMoreFailed to "Impossibile caricare altri record.",
    UiTextKey.HistoryErrorRemoveFavoriteFailed to "Impossibile rimuovere dai preferiti.",
    UiTextKey.HistoryErrorAddFavoriteFailed to "Impossibile aggiungere ai preferiti.",
    UiTextKey.HistoryErrorFavoriteSessionFailed to "Impossibile salvare la sessione nei preferiti.",
    UiTextKey.HistoryErrorUnfavoriteSessionFailed to "Impossibile rimuovere la sessione dai preferiti.",
    UiTextKey.TtsSpeaking to "Lettura in corso…",
    UiTextKey.TtsSpeakingTranslation to "Lettura della traduzione…",
    UiTextKey.TtsSpoken to "✓ Letto",
    UiTextKey.TtsError to "✗ {detail}",
    UiTextKey.SpeechStatusRecognitionError to "Errore di riconoscimento: {detail}",
    UiTextKey.SpeechStatusAutoDetecting to "Rilevamento automatico della lingua…",
    UiTextKey.SpeechStatusDetected to "Rilevato: {language}",
    UiTextKey.SpeechStatusDetectedConfidence to "Rilevato: {language} (affidabilità {confidence}%)",
    UiTextKey.SpeechStatusEnterText to "Inserisci o pronuncia il testo da tradurre.",
    UiTextKey.SpeechStatusLoginRequired to "Accedi per usare la traduzione.",
    UiTextKey.SpeechStatusDetectFailed to "Impossibile rilevare la lingua di origine. Selezionala manualmente.",
    UiTextKey.SpeechStatusTranslationError to "Errore di traduzione: {detail}",
    UiTextKey.SpeechStatusScanningImage to "Scansione del testo nell'immagine…",
    UiTextKey.SpeechStatusTextExtracted to "Testo estratto correttamente",
    UiTextKey.SpeechStatusOcrError to "Errore di scansione dell'immagine: {detail}",
    UiTextKey.SpeechStatusEnterTextFirst to "Inserisci o pronuncia prima un testo.",
    UiTextKey.SpeechStatusDetectFailedForSpeech to
        "Impossibile rilevare la lingua di origine per la lettura. Seleziona una lingua di origine.",
    UiTextKey.SpeechStatusRefreshed to "Aggiornato. Il rilevamento automatico ripartirà alla prossima traduzione o lettura.",
    UiTextKey.ContinuousStatusLoginRequired to "Accedi per usare la traduzione continua.",
    UiTextKey.ContinuousStatusPreparingMic to "Preparazione del microfono…",
    UiTextKey.ContinuousStatusTranslationError to "Errore di traduzione continua: {detail}",
    UiTextKey.ContinuousStatusRateLimited to "{detail} La modalità continua è stata interrotta per evitare errori ripetuti.",
    UiTextKey.ContinuousStatusRecognitionError to "Errore di riconoscimento continuo: {detail}",
    UiTextKey.ContinuousStatusStartError to "Impossibile avviare la modalità continua: {detail}",

    // Phase 1 part 3: social area messages
    UiTextKey.StatusRaw to "{detail}",
    UiTextKey.ErrGeneric to "Qualcosa è andato storto. Riprova.",
    UiTextKey.ErrNoInternet to "Nessuna connessione a Internet. Controlla la rete e riprova.",
    UiTextKey.ErrPermissionDenied to "Non hai il permesso di eseguire questa azione.",
    UiTextKey.ErrSessionExpired to "La sessione è scaduta. Accedi di nuovo.",
    UiTextKey.ErrItemNotFound to "L'elemento richiesto non è stato trovato. Potrebbe essere stato eliminato.",
    UiTextKey.ErrAccessDenied to "Accesso negato.",
    UiTextKey.ErrAlreadyFriends to "Sei già amico di questo utente.",
    UiTextKey.ErrUserMaybeBlocked to "Impossibile completare l'azione. L'utente potrebbe essere bloccato.",
    UiTextKey.ErrRequestGone to "Questa richiesta di amicizia non esiste più.",
    UiTextKey.ErrRequestHandled to "Questa richiesta è già stata gestita da qualcun altro.",
    UiTextKey.ErrNotAuthorized to "Non sei autorizzato a eseguire questa azione.",
    UiTextKey.ErrTooManyRequests to "Troppe richieste. Attendi un momento e riprova.",
    UiTextKey.ErrInvalidInput to "Input non valido. Controlla e riprova.",
    UiTextKey.ErrCannotCompleteNow to "Impossibile completare l'operazione in questo momento.",
    UiTextKey.ErrTimedOut to "L'operazione è scaduta. Riprova.",
    UiTextKey.FriendsMsgRemoved to "Amico rimosso.",
    UiTextKey.FriendsMsgRemoveFailed to "Impossibile rimuovere l'amico. Controlla la connessione e riprova.",
    UiTextKey.FriendsMsgRemovedMany to "Amico/i rimosso/i.",
    UiTextKey.FriendsMsgNeedUsername to "Imposta un nome utente nel profilo prima di gestire le richieste di amicizia.",
    UiTextKey.FriendsMsgMaxPending to
        "Hai raggiunto il massimo di {limit} richieste di amicizia in sospeso. Attendi che alcune vengano " +
            "accettate o annullane alcune prima di inviarne altre.",
    UiTextKey.FriendsMsgRateLimit to
        "Limite superato. Puoi inviare fino a {limit} richieste di amicizia all'ora. Riprova tra circa {minutes} " +
            "minuto/i.",
    UiTextKey.FriendsMsgNoteTooLong to "La nota non deve superare {max} caratteri",
    UiTextKey.FriendsMsgRequestSent to "Richiesta di amicizia inviata! Riceverà una notifica.",
    UiTextKey.FriendsMsgAlreadyFriends to "Sei già amico di questo utente.",
    UiTextKey.FriendsMsgRequestPending to
        "Hai già una richiesta in sospeso per questo utente. Attendi la sua risposta.",
    UiTextKey.FriendsMsgProfileMissing to "Profilo dell'utente non trovato. Potrebbe aver eliminato l'account.",
    UiTextKey.FriendsMsgYouBlocked to "Hai bloccato questo utente. Sbloccalo prima.",
    UiTextKey.FriendsMsgUnableToSend to "Impossibile inviare la richiesta di amicizia.",
    UiTextKey.FriendsMsgSendFailed to "Invio della richiesta di amicizia non riuscito. Riprova.",
    UiTextKey.FriendsMsgAccepted to "Richiesta di amicizia accettata! Ora siete amici.",
    UiTextKey.FriendsMsgAcceptGone to "Questa richiesta non esiste più — potrebbe essere stata annullata.",
    UiTextKey.FriendsMsgAcceptNotAuthorized to "Non sei autorizzato ad accettare questa richiesta.",
    UiTextKey.FriendsMsgAcceptFailed to "Accettazione della richiesta non riuscita. Riprova.",
    UiTextKey.FriendsMsgDeclined to "Richiesta rifiutata.",
    UiTextKey.FriendsMsgDeclineGone to "Questa richiesta non esiste più.",
    UiTextKey.FriendsMsgDeclineFailed to "Rifiuto della richiesta non riuscito. Riprova.",
    UiTextKey.FriendsMsgCancelled to "Richiesta di amicizia annullata.",
    UiTextKey.FriendsMsgCancelGone to
        "Questa richiesta non esiste più — potrebbe essere già stata accettata o rifiutata.",
    UiTextKey.FriendsMsgCancelFailed to "Annullamento della richiesta non riuscito. Riprova.",
    UiTextKey.FriendsMsgBatchAcceptAll to "Tutte le {count} richieste di amicizia accettate!",
    UiTextKey.FriendsMsgBatchAcceptFailed to "Accettazione delle richieste non riuscita. Riprova.",
    UiTextKey.FriendsMsgBatchAcceptMixed to "{success} accettate, {failed} non riuscite. Riprova quelle non riuscite.",
    UiTextKey.FriendsMsgBatchAcceptStoppedAt to "Accettazione multipla annullata dopo {processed}/{total} richiesta/e.",
    UiTextKey.FriendsMsgBatchAcceptStopped to "Accettazione multipla annullata.",
    UiTextKey.FriendsMsgBatchDeclined to "{count} richiesta/e rifiutata/e.",
    UiTextKey.FriendsMsgBatchDeclineStoppedAt to "Rifiuto multiplo annullato dopo {processed}/{total} richiesta/e.",
    UiTextKey.FriendsMsgBatchDeclineStopped to "Rifiuto multiplo annullato.",
    UiTextKey.FriendsMsgBlockedRemoved to "Utente bloccato e rimosso dagli amici.",
    UiTextKey.FriendsMsgBlockFailed to "Blocco dell'utente non riuscito. Riprova.",
    UiTextKey.FriendsMsgBlocked to "Utente bloccato.",
    UiTextKey.FriendsMsgUnblocked to "Utente sbloccato.",
    UiTextKey.FriendsMsgUnblockFailed to "Sblocco dell'utente non riuscito. Riprova.",
    UiTextKey.ChatMsgLoadFailed to "Caricamento dei messaggi non riuscito. Riprova.",
    UiTextKey.ChatMsgBlockFailed to "Blocco dell'utente non riuscito.",
    UiTextKey.ChatMsgUnblockFailed to "Sblocco dell'utente non riuscito.",
    UiTextKey.ChatMsgTooLong to "Il messaggio non deve superare {max} caratteri",
    UiTextKey.ChatMsgTooFast to "Stai inviando messaggi troppo velocemente. Attendi un momento.",
    UiTextKey.ChatMsgSendFailed to "Invio del messaggio non riuscito",
    UiTextKey.ChatMsgTranslateFailed to "Traduzione non riuscita. Riprova.",
    UiTextKey.MyProfileMsgLoadFailed to "Caricamento del profilo non riuscito. Riprova.",
    UiTextKey.MyProfileMsgNeedUsername to "Imposta un nome utente prima di rendere pubblico il profilo.",
    UiTextKey.MyProfileMsgPublic to "Profilo impostato come pubblico",
    UiTextKey.MyProfileMsgPrivate to "Profilo impostato come privato",
    UiTextKey.MyProfileMsgVisibilityFailed to "Aggiornamento della visibilità non riuscito. Riprova.",
    UiTextKey.SharedMsgAdded to "Aggiunto alla tua raccolta",
    UiTextKey.SharedMsgDeleted to "Elemento eliminato",
    UiTextKey.SharedMsgDismissed to "Elemento ignorato",
    UiTextKey.SharedMsgAcceptFailed to "Accettazione dell'elemento non riuscita",
    UiTextKey.SharedMsgDeleteFailed to "Eliminazione dell'elemento non riuscita",
    UiTextKey.SharedMsgDismissFailed to "Impossibile ignorare l'elemento",
    UiTextKey.SharedMsgNoItemId to "Nessun ID elemento",
    UiTextKey.SharedMsgLoadFailed to "Caricamento del materiale condiviso non riuscito. Riprova.",

    // Phase 1 part 4: settings, shop, profile, login and feedback messages
    UiTextKey.FeedbackMsgEmpty to "Il messaggio non può essere vuoto",
    UiTextKey.FeedbackMsgTooShort to "Il feedback deve contenere almeno {min} caratteri",
    UiTextKey.FeedbackMsgTooLong to "Il feedback non deve superare {max} caratteri",
    UiTextKey.FeedbackMsgRateLimited to "Hai inviato troppi feedback di recente. Riprova più tardi.",
    UiTextKey.FeedbackMsgSubmitFailed to "Invio del feedback non riuscito. Controlla la connessione a Internet.",
    UiTextKey.ShopMsgLoadFailed to "Impossibile caricare i dati del negozio",
    UiTextKey.ShopMsgPurchaseFailed to "Acquisto non riuscito",
    UiTextKey.ShopMsgHistoryExpanded to "Limite della cronologia esteso a {limit} voci!",
    UiTextKey.ShopMsgPaletteAlreadyUnlocked to "Tavolozza già sbloccata",
    UiTextKey.ShopMsgUnlockFailed to "Impossibile sbloccare la tavolozza",
    UiTextKey.ShopMsgPaletteUnlocked to "Tavolozza sbloccata!",
    UiTextKey.ShopMsgPaletteSelectFailed to "Impossibile cambiare la tavolozza dei colori. Riprova.",
    UiTextKey.ProfileMsgLoadFailed to "Impossibile caricare il profilo",
    UiTextKey.ProfileMsgUsernameTooShort to "Il nome utente deve contenere almeno {min} caratteri",
    UiTextKey.ProfileMsgUsernameTooLong to "Il nome utente non deve superare {max} caratteri",
    UiTextKey.ProfileMsgUsernameInvalidChars to "Il nome utente può contenere solo lettere, numeri e trattini bassi",
    UiTextKey.ProfileMsgVerifyFailed to "Impossibile verificare lo stato dell'account. Riprova.",
    UiTextKey.ProfileMsgUsernameTaken to "Nome utente già in uso",
    UiTextKey.ProfileMsgUsernameUpdatedSyncing to
        "Nome utente aggiornato. L'elenco degli amici potrebbe impiegare un momento ad aggiornarsi.",
    UiTextKey.ProfileMsgUsernameUpdated to "Nome utente aggiornato",
    UiTextKey.ProfileMsgUpdateFailed to "Impossibile aggiornare il profilo",
    UiTextKey.ProfileMsgSetUsernameFailed to "Impossibile impostare il nome utente",
    UiTextKey.ProfileMsgDeleteFailed to "Impossibile eliminare l'account",
    UiTextKey.ProfileMsgInvalidPassword to "Password errata. Riprova.",
    UiTextKey.ProfileMsgGoogleReauthFailed to "Nuova autenticazione Google non riuscita. Riprova.",
    UiTextKey.ProfileMsgGoogleNoToken to "Accesso con Google non riuscito: nessun token ID.",
    UiTextKey.ProfileMsgGoogleSignInFailed to "Accesso con Google non riuscito. Riprova.",
    UiTextKey.ProfileGoogleReauthHint to "Esegui di nuovo l'autenticazione con il tuo account Google per confermare.",
    UiTextKey.ProfileContinueWithGoogle to "Continua con Google",
    UiTextKey.SettingsMsgLanguageSaveFailed to "Impossibile salvare la lingua. Riprova.",
    UiTextKey.SettingsMsgFontSaveFailed to "Impossibile salvare la dimensione del testo. Riprova.",
    UiTextKey.SettingsMsgThemeSaveFailed to "Impossibile salvare il tema. Riprova.",
    UiTextKey.SettingsMsgAutoThemeFailed to "Impossibile disattivare il tema automatico. Riprova.",
    UiTextKey.SettingsMsgVoiceSaveFailed to "Impossibile salvare la voce. Riprova.",
    UiTextKey.SettingsMsgNotificationSaveFailed to "Impossibile salvare le notifiche. Riprova.",
    UiTextKey.AuthMsgEmailEmpty to "L'email non può essere vuota",
    UiTextKey.AuthMsgEmailInvalid to "Formato email non valido",
    UiTextKey.AuthMsgTooManyLogins to "Troppi tentativi di accesso. Riprova più tardi.",
    UiTextKey.AuthMsgTooManyResets to "Troppi tentativi di reimpostazione della password. Riprova più tardi.",
    UiTextKey.AuthMsgGoogleEmptyToken to "Accesso con Google non riuscito: token ID vuoto.",
    UiTextKey.AuthMsgGoogleNoToken to "Accesso con Google non riuscito: nessun token ID ricevuto.",
    UiTextKey.AuthMsgGoogleFailedCode to "Accesso con Google non riuscito (codice {code}).",
    UiTextKey.AuthMsgGoogleNotConfigured to "L'accesso con Google non è configurato per questa build.",
    UiTextKey.ErrUnknown to "Si è verificato un errore sconosciuto.",
    UiTextKey.ErrNetworkFailed to "Connessione di rete non riuscita. Controlla la connessione a Internet.",
    UiTextKey.ErrOperationTimedOut to "Operazione scaduta. Riprova.",
    UiTextKey.ErrCancelled to "Operazione annullata.",
    UiTextKey.ErrUnexpected to "Si è verificato un errore imprevisto. Riprova.",
    UiTextKey.ErrAuthInvalidEmail to "L'indirizzo email non è nel formato corretto.",
    UiTextKey.ErrAuthWrongPassword to "Password errata.",
    UiTextKey.ErrAuthUserNotFound to "Nessun account trovato con questa email.",
    UiTextKey.ErrAuthUserDisabled to "Questo account è stato disattivato.",
    UiTextKey.ErrAuthTooManyRequests to "Troppe richieste. Riprova più tardi.",
    UiTextKey.ErrAuthEmailInUse to "Questo indirizzo email è già usato da un altro account.",
    UiTextKey.ErrAuthFailed to "Autenticazione non riuscita. Riprova.",
    UiTextKey.ErrDbPermission to "Non hai l'autorizzazione per accedere a questi dati.",
    UiTextKey.ErrDbUnavailable to "Servizio al momento non disponibile. Riprova più tardi.",
    UiTextKey.ErrDbGeneric to "Errore del database. Riprova.",

    // Phase 1 part 5: learning, word bank, custom words, favorites and empty states
    UiTextKey.LearningMsgLoadFailed to "Impossibile caricare le schede di studio.",
    UiTextKey.LearningMsgSheetLoadFailed to "Impossibile caricare la scheda di studio.",
    UiTextKey.LearningMsgSheetGenerateCancelled to "Generazione della scheda di studio annullata.",
    UiTextKey.LearningMsgSheetGenerateFailed to "Impossibile generare la scheda di studio. Riprova.",
    UiTextKey.LearningMsgQuizParseIncomplete to "Il quiz ha solo {parsed} domande su {total}. Rigeneralo.",
    UiTextKey.LearningMsgQuizGenerateCancelled to "Generazione del quiz annullata.",
    UiTextKey.LearningMsgQuizGenerateFailed to "Impossibile generare il quiz. Riprova.",
    UiTextKey.LearningMsgShareNotReady to "Il contenuto della scheda non è ancora pronto. Attendi il caricamento.",
    UiTextKey.QuizMsgNotGenerated to "Nessun quiz ancora generato.",
    UiTextKey.QuizMsgCorrupted to "Il quiz salvato è danneggiato. Rigeneralo.",
    UiTextKey.QuizMsgLoadFailed to "Impossibile caricare il quiz.",
    UiTextKey.QuizMsgSubmitFailed to "Impossibile inviare il quiz. Riprova.",
    UiTextKey.ShareMsgFailed to "Impossibile condividere. Riprova.",
    UiTextKey.WordBankMsgLoadFailed to "Impossibile caricare la banca di parole.",
    UiTextKey.WordBankMsgDeleteFailed to "Impossibile eliminare la parola. Riprova.",
    UiTextKey.WordBankMsgNeedMoreRecords to "Servono altri {count} record per aggiornare la banca di parole",
    UiTextKey.WordBankMsgNoHistory to "Nessuna cronologia di traduzione per questa lingua",
    UiTextKey.WordBankMsgGenerateFailed to "Impossibile generare la banca di parole. Riprova.",
    UiTextKey.WordBankMsgShareIncomplete to "I dati della parola sono incompleti. Aggiorna e riprova.",
    UiTextKey.CustomWordsMsgOriginalRequired to "La parola originale è obbligatoria",
    UiTextKey.CustomWordsMsgOriginalTooLong to "La parola originale non deve superare {max} caratteri",
    UiTextKey.CustomWordsMsgTranslatedRequired to "La parola tradotta è obbligatoria",
    UiTextKey.CustomWordsMsgTranslatedTooLong to "La parola tradotta non deve superare {max} caratteri",
    UiTextKey.CustomWordsMsgPronunciationTooLong to "La pronuncia non deve superare {max} caratteri",
    UiTextKey.CustomWordsMsgExampleTooLong to "L'esempio non deve superare {max} caratteri",
    UiTextKey.CustomWordsMsgDuplicate to "Questa parola esiste già nella tua banca di parole personalizzata",
    UiTextKey.CustomWordsMsgAddFailed to "Impossibile aggiungere la parola",
    UiTextKey.CustomWordsMsgInvalidLanguageUpdate to "Richiesta di aggiornamento della lingua non valida",
    UiTextKey.CustomWordsMsgTranslateFailed to "Traduzione non riuscita: {detail}",
    UiTextKey.CustomWordsMsgUpdateLanguageFailed to "Impossibile aggiornare la lingua della parola",
    UiTextKey.CustomWordsMsgLoadFailed to "Impossibile caricare le parole personalizzate",
    UiTextKey.FavoritesMsgLoadFailed to "Impossibile caricare i preferiti",
    UiTextKey.FavoritesMsgLoadMoreFailed to "Impossibile caricare altri preferiti",
    UiTextKey.FavoritesMsgRefreshFailed to "Impossibile aggiornare i preferiti",
    UiTextKey.FavoritesMsgRemoveFailed to "Impossibile rimuovere il preferito",
    UiTextKey.FavoritesMsgDeleteSelectedFailed to "Impossibile eliminare gli elementi selezionati",
    UiTextKey.FavoritesLoadMore to "Carica altro",
    UiTextKey.A11yConfirmDelete to "Conferma eliminazione",
    UiTextKey.A11yDeleteMode to "Modalità eliminazione",
    UiTextKey.A11yExpand to "Espandi",
    UiTextKey.A11yCollapse to "Comprimi",
    UiTextKey.EmptyNoHistoryTitle to "Nessuna cronologia",
    UiTextKey.EmptyNoFavoritesTitle to "Nessun preferito",
    UiTextKey.EmptyNoLearningTitle to "Nessun materiale di studio",
    UiTextKey.EmptySharedItemsMessage to "Nessun elemento condiviso con te",
    UiTextKey.SharedMaterialLoadingContent to "Caricamento contenuto...",
    UiTextKey.HelpVersionTemplate to "Versione: {version}",
    UiTextKey.StartupTagline to "Traduci e impara",

    // Extended Error Messages
)
