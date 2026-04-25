package com.example.fyp.model.ui

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
    UiTextKey.RecognizingStatus to "Registrando...parla pure, si fermerà automaticamente",
    UiTextKey.TranslatingStatus to "Traducendo...",
    UiTextKey.SpeakingOriginalStatus to "Lettura testo originale...",
    UiTextKey.SpeakingTranslationStatus to "Lettura traduzione...",
    UiTextKey.SpeakingLabel to "In riproduzione",
    UiTextKey.FinishedSpeakingOriginal to "Lettura testo originale completata",
    UiTextKey.FinishedSpeakingTranslation to "Lettura traduzione completata",
    UiTextKey.TtsErrorTemplate to "Errore vocale: %s",

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
    UiTextKey.StatusAzureErrorTemplate to "Errore Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Errore traduzione: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Accedi per tradurre",
    UiTextKey.StatusRecognizePreparing to "Preparazione microfono...(non parlare ancora)",
    UiTextKey.StatusRecognizeListening to "Ascolto...parla pure",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Precedente",
    UiTextKey.PaginationNextLabel to "Successivo",
    UiTextKey.PaginationPageLabelTemplate to "Pagina {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "Copiato",
    UiTextKey.DisableText to "Accedi per tradurre e salvare la cronologia",

    // Error
    UiTextKey.ErrorRetryButton to "Riprova",
    UiTextKey.ErrorGenericMessage to "Si è verificato un errore. Riprova",

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
    UiTextKey.HomeFeaturesTitle to "Funzionalità",
    UiTextKey.HomeDiscreteDescription to "Traduci testi e audio brevi",
    UiTextKey.HomeContinuousDescription to "Conversazione bilingue in tempo reale",
    UiTextKey.HomeLearningDescription to "Crea materiali e quiz dalla cronologia",

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
    UiTextKey.SettingsColorPaletteTitle to "Temi colore",
    UiTextKey.SettingsColorPaletteDesc to "Scegli un tema colore. 10 monete ciascuno",
    UiTextKey.SettingsColorCostTemplate to "{cost} monete",
    UiTextKey.SettingsColorUnlockButton to "Sblocca",
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
    UiTextKey.SettingsVoiceTitle to "Impostazioni voce",
    UiTextKey.SettingsVoiceDesc to "Scegli la voce per ogni lingua",
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
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Servono ancora {needed} voci per rigenerare (necessarie 5 in più)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Il conteggio deve essere superiore all'ultima generazione",
    UiTextKey.LearningRegenInfoTitle to "Regole di rigenerazione",
    UiTextKey.LearningRegenInfoMessage to "Rigenerare materiali:\n\n• Prima volta: in qualsiasi momento\n• Rigenerare: servono 5 voci in più\n\nIl pulsante diventerà verde quando pronto. Se grigio, fai più traduzioni!\n\n💡 Suggerimento: Se il conteggio non si aggiorna, riavvia l'app",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Quiz già generato per questa versione. Rigenera i materiali per un nuovo quiz",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz: {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Genera quiz",
    UiTextKey.QuizGenerating to "⏳ Generazione...",
    UiTextKey.QuizUpToDate to "✓ Aggiornato",
    UiTextKey.QuizBlocked to "🚫 Bloccato",
    UiTextKey.QuizWait to "⏳ Attendi...",
    UiTextKey.QuizMaterialsQuizTemplate to "Materiale: {materials} | Quiz: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Puoi guadagnare monete!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 Servono ancora {count} per le monete",
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
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Impossibile rigenerare: materiale({materials}) < quiz({quiz}), fai più traduzioni",
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
    UiTextKey.QuizOutdatedMessage to "Questo quiz usa materiali obsoleti",
    UiTextKey.QuizRecordsLabel to "Voci",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Le mie monete",
    UiTextKey.HistoryCoinRulesTitle to "Regole monete:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Come guadagnare:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 moneta per risposta corretta",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Solo il primo tentativo per versione",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Il quiz deve corrispondere al materiale attuale",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Regole anti-frode:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Servono 10 nuove traduzioni dall'ultimo premio",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• La versione del quiz deve corrispondere al materiale",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Eliminare voci blocca la rigenerazione (tranne se il conteggio è superiore)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Nessuna moneta nei tentativi successivi",
    UiTextKey.HistoryCoinTipsTitle to "💡 Suggerimenti:",
    UiTextKey.HistoryCoinTipsRule1 to "• Aggiungi traduzioni regolarmente",
    UiTextKey.HistoryCoinTipsRule2 to "• Studia bene prima del primo tentativo!",
    UiTextKey.HistoryCoinGotItButton to "Ho capito!",

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
    UiTextKey.WordBankRegenerate to "Rigenera banca parole",
    UiTextKey.WordBankRefresh to "🔄 Aggiorna banca",
    UiTextKey.WordBankEmpty to "Banca parole vuota",
    UiTextKey.WordBankEmptyHint to "Tocca sopra per generare dalla cronologia",
    UiTextKey.WordBankExample to "Esempio:",
    UiTextKey.WordBankDifficulty to "Livello:",
    UiTextKey.WordBankFilterCategory to "Categoria",
    UiTextKey.WordBankFilterCategoryAll to "Tutte le categorie",
    UiTextKey.WordBankFilterDifficultyLabel to "Livello:",
    UiTextKey.WordBankFilterNoResults to "Nessuna parola corrisponde al filtro",
    UiTextKey.WordBankRefreshAvailable to "✅ Aggiornamento disponibile!",
    UiTextKey.WordBankRecordsNeeded to "Voci (servono 20 per aggiornare)",
    UiTextKey.WordBankRegenInfoTitle to "Regole di aggiornamento",
    UiTextKey.WordBankRegenInfoMessage to "Aggiornare la banca parole:\n\n• Prima volta: in qualsiasi momento\n• Aggiornare: servono 20 voci in più\n\nIl pulsante diventerà verde quando pronto. Se grigio, fai più traduzioni!\n\n💡 Suggerimento: Se il conteggio non si aggiorna, riavvia l'app",
    UiTextKey.WordBankHistoryCountTemplate to "Conteggio attuale: {nowCount} (alla generazione: {savedCount})",

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
    UiTextKey.ProfileUpdateSuccess to "Profilo aggiornato",
    UiTextKey.ProfileUpdateError to "Aggiornamento fallito",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Elimina account",
    UiTextKey.AccountDeleteWarning to "⚠️ Permanente e irreversibile!",
    UiTextKey.AccountDeleteConfirmMessage to "Tutti i dati saranno eliminati permanentemente: cronologia, banca parole, materiali, impostazioni. Inserisci la password per confermare",
    UiTextKey.AccountDeletePasswordLabel to "Password",
    UiTextKey.AccountDeleteButton to "Elimina account",
    UiTextKey.AccountDeleteSuccess to "Account eliminato con successo",
    UiTextKey.AccountDeleteError to "Eliminazione fallita",
    UiTextKey.AccountDeleteReauthRequired to "Inserisci la password per confermare l'eliminazione",

    // Favorites
    UiTextKey.FavoritesTitle to "Preferiti",
    UiTextKey.FavoritesEmpty to "Nessun preferito ancora",
    UiTextKey.FavoritesAddSuccess to "Aggiunto ai preferiti",
    UiTextKey.FavoritesRemoveSuccess to "Rimosso dai preferiti",
    UiTextKey.FavoritesAddButton to "Aggiungi ai preferiti",
    UiTextKey.FavoritesRemoveButton to "Rimuovi dai preferiti",
    UiTextKey.FavoritesNoteLabel to "Nota",
    UiTextKey.FavoritesNoteHint to "Aggiungi nota (opzionale)",
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
    UiTextKey.CustomWordsSaveSuccess to "Parola salvata",
    UiTextKey.CustomWordsDeleteSuccess to "Parola eliminata",
    UiTextKey.CustomWordsAlreadyExists to "Parola già esistente",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Lingua originale",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Lingua traduzione",
    UiTextKey.CustomWordsSaveButton to "Salva",
    UiTextKey.CustomWordsCancelButton to "Annulla",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Rilevamento automatico",
    UiTextKey.LanguageDetectDetecting to "Rilevamento...",
    UiTextKey.LanguageDetectedTemplate to "Rilevato: {language}",
    UiTextKey.LanguageDetectFailed to "Rilevamento fallito",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Scansiona testo dall'immagine",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Avviso: Il riconoscimento testo potrebbe non essere completamente preciso. Rivedi il testo rilevato. " +
            "Supporta Latino (Inglese ecc.), Cinese, Giapponese e Coreano",
    UiTextKey.ImageRecognitionScanning to "Scansione testo...",
    UiTextKey.ImageRecognitionSuccess to "Testo rilevato con successo",

    // Cache
    UiTextKey.CacheClearButton to "Svuota cache",
    UiTextKey.CacheClearSuccess to "Cache svuotata",
    UiTextKey.CacheStatsTemplate to "Cache: {count} traduzioni salvate",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Tema automatico",
    UiTextKey.SettingsAutoThemeDesc to "Alterna chiaro/scuro automaticamente per orario",
    UiTextKey.SettingsAutoThemeEnabled to "Attivato",
    UiTextKey.SettingsAutoThemeDisabled to "Disattivato",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Inizio scuro:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Inizio chiaro:",
    UiTextKey.SettingsAutoThemePreview to "Il tema cambierà automaticamente secondo l'orario impostato",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Modalità offline",
    UiTextKey.OfflineModeMessage to "Sei offline. Verranno mostrati i dati in cache",
    UiTextKey.OfflineModeRetry to "Riprova connessione",
    UiTextKey.OfflineDataCached to "Dati in cache disponibili",
    UiTextKey.OfflineSyncPending to "Le modifiche saranno sincronizzate quando online",

    // Image capture
    UiTextKey.ImageSourceTitle to "Scegli fonte immagine",
    UiTextKey.ImageSourceCamera to "Scatta foto",
    UiTextKey.ImageSourceGallery to "Scegli dalla galleria",
    UiTextKey.ImageSourceCancel to "Annulla",
    UiTextKey.CameraCaptureContentDesc to "Scatta foto",

    // Friends
    UiTextKey.FriendsTitle to "Amici",
    UiTextKey.FriendsMenuButton to "Amici",
    UiTextKey.FriendsAddButton to "Aggiungi amico",
    UiTextKey.FriendsSearchTitle to "Cerca utente",
    UiTextKey.FriendsSearchPlaceholder to "Nome utente o ID...",
    UiTextKey.FriendsSearchMinChars to "Inserisci almeno 2 caratteri",
    UiTextKey.FriendsSearchNoResults to "Nessun utente trovato",
    UiTextKey.FriendsListEmpty to "Aggiungi amici per chattare e condividere materiali",
    UiTextKey.FriendsRequestsSection to "Richieste ({count})",
    UiTextKey.FriendsSectionTitle to "Amici ({count})",
    UiTextKey.FriendsAcceptButton to "Accetta",
    UiTextKey.FriendsRejectButton to "Rifiuta",
    UiTextKey.FriendsRemoveButton to "Rimuovi",
    UiTextKey.FriendsRemoveDialogTitle to "Rimuovere amico",
    UiTextKey.FriendsRemoveDialogMessage to "Rimuovere {username} dalla lista amici?",
    UiTextKey.FriendsSendRequestButton to "Aggiungi",
    UiTextKey.FriendsRequestSentSuccess to "Richiesta inviata!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Richiesta accettata!",
    UiTextKey.FriendsRequestRejectedSuccess to "Richiesta rifiutata",
    UiTextKey.FriendsRemovedSuccess to "Amico rimosso",
    UiTextKey.FriendsRequestFailed to "Invio fallito",
    UiTextKey.FriendsCloseButton to "Chiudi",
    UiTextKey.FriendsCancelButton to "Annulla",
    UiTextKey.FriendsRemoveConfirm to "Rimuovi",
    UiTextKey.FriendsNewRequestsTemplate to "{count} nuove richieste!",
    UiTextKey.FriendsSentRequestsSection to "Inviate ({count})",
    UiTextKey.FriendsPendingStatus to "In attesa",
    UiTextKey.FriendsCancelRequestButton to "Annulla richiesta",
    UiTextKey.FriendsUnreadMessageDesc to "Invia messaggio",
    UiTextKey.FriendsDeleteModeButton to "Elimina amici",
    UiTextKey.FriendsDeleteSelectedButton to "Elimina selezionati",
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
    UiTextKey.ChatMessageSent to "Messaggio inviato",
    UiTextKey.ChatMessageFailed to "Invio fallito",
    UiTextKey.ChatMarkingRead to "Lettura...",
    UiTextKey.ChatLoadingMessages to "Caricamento messaggi...",
    UiTextKey.ChatToday to "Oggi",
    UiTextKey.ChatYesterday to "Ieri",
    UiTextKey.ChatUnreadBadge to "{count} non letti",
    UiTextKey.ChatTranslateButton to "Traduci",
    UiTextKey.ChatTranslateDialogTitle to "Traduci chat",
    UiTextKey.ChatTranslateDialogMessage to "Tradurre i messaggi dell'amico nella tua lingua? Ogni messaggio sarà rilevato e tradotto automaticamente",
    UiTextKey.ChatTranslateConfirm to "Traduci tutto",
    UiTextKey.ChatTranslating to "Traduzione messaggi...",
    UiTextKey.ChatTranslated to "Messaggi tradotti",
    UiTextKey.ChatShowOriginal to "Mostra originale",
    UiTextKey.ChatShowTranslation to "Mostra traduzione",
    UiTextKey.ChatTranslateFailed to "Traduzione fallita",
    UiTextKey.ChatTranslatedLabel to "Tradotto",

    // Sharing
    UiTextKey.ShareTitle to "Condividi",
    UiTextKey.ShareInboxTitle to "Posta condivisioni",
    UiTextKey.ShareInboxEmpty to "Nessuna condivisione ancora. Gli amici possono condividere parole e materiali!",
    UiTextKey.ShareWordButton to "Condividi parola",
    UiTextKey.ShareMaterialButton to "Condividi materiale",
    UiTextKey.ShareSelectFriendTitle to "Seleziona amico",
    UiTextKey.ShareSelectFriendMessage to "Seleziona un amico per condividere:",
    UiTextKey.ShareSuccess to "Condiviso con successo!",
    UiTextKey.ShareFailed to "Condivisione fallita",
    UiTextKey.ShareWordWith to "Condividi parola con {username}",
    UiTextKey.ShareMaterialWith to "Condividi materiale con {username}",
    UiTextKey.ShareAcceptButton to "Accetta",
    UiTextKey.ShareDismissButton to "Rifiuta",
    UiTextKey.ShareAccepted to "Aggiunto alla collezione",
    UiTextKey.ShareDismissed to "Elemento rifiutato",
    UiTextKey.ShareActionFailed to "Azione fallita",
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
    UiTextKey.MyProfileDisplayName to "Nome visualizzato",
    UiTextKey.MyProfileCopyUserId to "Copia ID",
    UiTextKey.MyProfileCopyUsername to "Copia nome utente",
    UiTextKey.MyProfileShare to "Condividi profilo",
    UiTextKey.MyProfileCopied to "Copiato!",
    UiTextKey.MyProfileLanguages to "Lingue",
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
    UiTextKey.LearningEmptyTitle to "Nessuna cronologia di traduzione",
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
    UiTextKey.ShareLearningSheetLanguageLabel to "Lingua: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Chiudi",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Connesso o in attesa",
    UiTextKey.AccessibilityNewMessages to "Nuovi messaggi",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicatore nuovi elementi",
    UiTextKey.AccessibilitySuccessIcon to "Successo",
    UiTextKey.AccessibilityErrorIcon to "Errore",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Tipo elemento condiviso",
    UiTextKey.AccessibilityAddCustomWords to "Aggiungi parole personalizzate",
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
    UiTextKey.FriendsNotifCloseButton to "Fatto",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Badge nell'app (punti rossi)",
    UiTextKey.InAppBadgeMessages to "Badge messaggi non letti",
    UiTextKey.InAppBadgeFriendRequests to "Badge richieste di amicizia",
    UiTextKey.InAppBadgeSharedInbox to "Badge posta condivisioni",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Accedi per continuare",
    UiTextKey.ErrorSaveFailedRetry to "Salvataggio fallito. Riprova",
    UiTextKey.ErrorLoadFailedRetry to "Caricamento fallito. Riprova",
    UiTextKey.ErrorNetworkRetry to "Errore di rete. Controlla la connessione",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Servono ancora {needed} traduzioni per generare il materiale",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Passa alla conversazione continua →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Cancella conversazione",
    UiTextKey.ChatClearConversationTitle to "Cancellare conversazione",
    UiTextKey.ChatClearConversationMessage to "Nascondere tutti i messaggi? Rimarranno nascosti alla riapertura. Non influisce sull'altra persona",
    UiTextKey.ChatClearConversationConfirm to "Cancella tutto",
    UiTextKey.ChatClearConversationSuccess to "Conversazione cancellata",

    // Block user
    UiTextKey.BlockUserButton to "Blocca",
    UiTextKey.BlockUserTitle to "Bloccare questo utente?",
    UiTextKey.BlockUserMessage to "Bloccare {username}? Verrà rimosso dalla lista e bloccato dalle interazioni",
    UiTextKey.BlockUserConfirm to "Blocca",
    UiTextKey.BlockUserSuccess to "Bloccato e rimosso dalla lista",
    UiTextKey.BlockedUsersTitle to "Utenti bloccati",
    UiTextKey.BlockedUsersEmpty to "Nessun utente bloccato",
    UiTextKey.UnblockUserButton to "Sblocca",
    UiTextKey.UnblockUserTitle to "Sbloccare?",
    UiTextKey.UnblockUserMessage to "Sbloccare {username}? Potranno inviare richieste di nuovo",
    UiTextKey.UnblockUserSuccess to "Sbloccato",
    UiTextKey.BlockedUsersManageButton to "Gestisci bloccati",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Nota richiesta (opzionale)",
    UiTextKey.FriendsRequestNotePlaceholder to "Aggiungi nota breve...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Materiale pronto! Tocca per aprire",
    UiTextKey.GenerationBannerWordBank to "Banca parole pronta! Tocca per vedere",
    UiTextKey.GenerationBannerQuiz to "Quiz pronto! Tocca per iniziare",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Notifiche",

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
    UiTextKey.AddCustomWordHintTemplate to "Inserisci la parola in {from} e la traduzione in {to}",

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

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Nessuna connessione internet. Controlla la connessione",
    UiTextKey.ErrorPermissionDenied to "Permesso negato per questa azione",
    UiTextKey.ErrorSessionExpired to "Sessione scaduta. Accedi di nuovo",
    UiTextKey.ErrorItemNotFound to "Elemento non trovato. Potrebbe essere stato eliminato",
    UiTextKey.ErrorAccessDenied to "Accesso negato",
    UiTextKey.ErrorAlreadyFriends to "Già amici",
    UiTextKey.ErrorUserBlocked to "Non consentito. L'utente potrebbe essere bloccato",
    UiTextKey.ErrorRequestNotFound to "La richiesta non esiste più",
    UiTextKey.ErrorRequestAlreadyHandled to "La richiesta è già stata gestita",
    UiTextKey.ErrorNotAuthorized to "Non hai i permessi per farlo",
    UiTextKey.ErrorRateLimited to "Troppe richieste. Riprova dopo",
    UiTextKey.ErrorInvalidInput to "Input non valido. Controlla e riprova",
    UiTextKey.ErrorOperationNotAllowed to "Questa operazione non è consentita al momento",
    UiTextKey.ErrorTimeout to "Tempo scaduto. Riprova",
    UiTextKey.ErrorSendMessageFailed to "Invio messaggio fallito. Riprova",
    UiTextKey.ErrorFriendRequestSent to "Richiesta già inviata!",
    UiTextKey.ErrorFriendRequestFailed to "Invio richiesta fallito",
    UiTextKey.ErrorFriendRemoved to "Amico rimosso",
    UiTextKey.ErrorFriendRemoveFailed to "Rimozione fallita. Controlla la connessione",
    UiTextKey.ErrorBlockSuccess to "Utente bloccato",
    UiTextKey.ErrorBlockFailed to "Blocco fallito. Riprova",
    UiTextKey.ErrorUnblockSuccess to "Sbloccato",
    UiTextKey.ErrorUnblockFailed to "Sblocco fallito. Riprova",
    UiTextKey.ErrorAcceptRequestSuccess to "Richiesta accettata!",
    UiTextKey.ErrorAcceptRequestFailed to "Accettazione fallita. Riprova",
    UiTextKey.ErrorRejectRequestSuccess to "Richiesta rifiutata",
    UiTextKey.ErrorRejectRequestFailed to "Rifiuto fallito. Riprova",
    UiTextKey.ErrorOfflineMessage to "Sei offline. Alcune funzionalità potrebbero non essere disponibili",
    UiTextKey.ErrorChatDeletionFailed to "Cancellazione conversazione fallita. Riprova",
    UiTextKey.ErrorGenericRetry to "Si è verificato un errore. Riprova",
)
