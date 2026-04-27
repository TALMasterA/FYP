package com.translator.TalknLearn.model.ui

/**
 * German (de-DE) UI text map — Deutsche Benutzeroberfläche.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val DeDeUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Mikrofon verwenden",
    UiTextKey.CopyButton to "Kopieren",
    UiTextKey.SpeakScriptButton to "Originaltext vorlesen",
    UiTextKey.TranslateButton to "Übersetzen",
    UiTextKey.CopyTranslationButton to "Übersetzung kopieren",
    UiTextKey.SpeakTranslationButton to "Übersetzung vorlesen",
    UiTextKey.RecognizingStatus to "Aufnahme...Sprechen Sie jetzt, automatischer Stopp.",
    UiTextKey.TranslatingStatus to "Übersetze...",
    UiTextKey.SpeakingOriginalStatus to "Original wird vorgelesen...",
    UiTextKey.SpeakingTranslationStatus to "Übersetzung wird vorgelesen...",
    UiTextKey.SpeakingLabel to "Vorlesen",
    UiTextKey.FinishedSpeakingOriginal to "Original fertig vorgelesen",
    UiTextKey.FinishedSpeakingTranslation to "Übersetzung fertig vorgelesen",
    UiTextKey.TtsErrorTemplate to "Sprachfehler: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "App-Sprache",
    UiTextKey.DetectLanguageLabel to "Sprache erkennen",
    UiTextKey.TranslateToLabel to "Übersetzen nach",

    // Language names
    UiTextKey.LangEnUs to "Englisch",
    UiTextKey.LangZhHk to "Kantonesisch",
    UiTextKey.LangJaJp to "Japanisch",
    UiTextKey.LangZhCn to "Chinesisch (vereinfacht)",
    UiTextKey.LangFrFr to "Französisch",
    UiTextKey.LangDeDe to "Deutsch",
    UiTextKey.LangKoKr to "Koreanisch",
    UiTextKey.LangEsEs to "Spanisch",
    UiTextKey.LangIdId to "Indonesisch",
    UiTextKey.LangViVn to "Vietnamesisch",
    UiTextKey.LangThTh to "Thailändisch",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Malaiisch",
    UiTextKey.LangPtBr to "Portugiesisch",
    UiTextKey.LangItIt to "Italienisch",
    UiTextKey.LangRuRu to "Russisch",

    // Navigation
    UiTextKey.NavHistory to "Verlauf",
    UiTextKey.NavLogin to "Anmelden",
    UiTextKey.NavLogout to "Abmelden",
    UiTextKey.NavBack to "Zurück",
    UiTextKey.ActionCancel to "Abbrechen",
    UiTextKey.ActionDelete to "Löschen",
    UiTextKey.ActionOpen to "Öffnen",
    UiTextKey.ActionName to "Benennen",
    UiTextKey.ActionSave to "Speichern",
    UiTextKey.ActionConfirm to "Bestätigen",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Hier eingeben oder Mikrofon verwenden...",
    UiTextKey.SpeechTranslatedPlaceholder to "Übersetzung erscheint hier...",
    UiTextKey.StatusAzureErrorTemplate to "Azure-Fehler: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Übersetzungsfehler: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Anmeldung zum Übersetzen erforderlich",
    UiTextKey.StatusRecognizePreparing to "Mikrofon wird vorbereitet...(nicht sprechen)",
    UiTextKey.StatusRecognizeListening to "Zuhören...Sprechen Sie jetzt.",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Vorherige Seite",
    UiTextKey.PaginationNextLabel to "Nächste Seite",
    UiTextKey.PaginationPageLabelTemplate to "Seite {page} von {total}",

    // Toast
    UiTextKey.ToastCopied to "Kopiert",
    UiTextKey.DisableText to "Melden Sie sich an, um zu übersetzen und den Verlauf zu speichern.",

    // Error
    UiTextKey.ErrorRetryButton to "Erneut versuchen",
    UiTextKey.ErrorGenericMessage to "Ein Fehler ist aufgetreten. Bitte erneut versuchen.",

    // Shop
    UiTextKey.ShopTitle to "Shop",
    UiTextKey.ShopCoinBalance to "Meine Münzen",
    UiTextKey.ShopHistoryExpansionTitle to "Verlaufserweiterung",
    UiTextKey.ShopHistoryExpansionDesc to "Erweitern Sie Ihr Verlaufslimit, um mehr Übersetzungen zu sehen.",
    UiTextKey.ShopCurrentLimit to "Aktuelles Limit: {limit} Einträge",
    UiTextKey.ShopMaxLimit to "Maximales Limit:",
    UiTextKey.ShopBuyHistoryExpansion to "Kaufen (+{increment} Einträge, {cost} Münzen)",
    UiTextKey.ShopInsufficientCoins to "Nicht genug Münzen",
    UiTextKey.ShopMaxLimitReached to "Maximales Limit erreicht",
    UiTextKey.ShopHistoryExpandedTitle to "Erfolgreich erweitert!",
    UiTextKey.ShopHistoryExpandedMessage to "Ihr Verlaufslimit beträgt jetzt {limit} Einträge! Sie können mehr Übersetzungen sehen!",
    UiTextKey.ShopColorPaletteTitle to "Farbthemen",
    UiTextKey.ShopColorPaletteDesc to "Wählen Sie Ihr App-Farbthema, je 10 Münzen",
    UiTextKey.ShopEntry to "Shop",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Stimmeinstellungen",
    UiTextKey.VoiceSettingsDesc to "Wählen Sie die Vorlesestimme für jede Sprache.",

    // Instructions
    UiTextKey.SpeechInstructions to "Tippen Sie auf das Mikrofon für Spracherkennung, dann auf Übersetzen. Wenn die automatische Erkennung nach Text- oder Sprachwechsel nicht aktualisiert, tippen Sie oben rechts auf Aktualisieren.",
    UiTextKey.HomeInstructions to "Wählen Sie eine Funktion zum Starten.",
    UiTextKey.ContinuousInstructions to "Wählen Sie zwei Sprachen und starten Sie den Gesprächsmodus.",

    // Home
    UiTextKey.HomeTitle to "Sofortübersetzung",
    UiTextKey.HelpTitle to "Hilfe",
    UiTextKey.SpeechTitle to "Schnellübersetzung",
    UiTextKey.HomeStartButton to "Übersetzung starten",
    UiTextKey.HomeFeaturesTitle to "Funktionen",
    UiTextKey.HomeDiscreteDescription to "Kurze Text- und Sprachübersetzung",
    UiTextKey.HomeContinuousDescription to "Bidirektionale Echtzeit-Übersetzung",
    UiTextKey.HomeLearningDescription to "Lernmaterial und Quiz aus dem Verlauf generieren",

    // Help
    UiTextKey.HelpCurrentTitle to "Aktuelle Funktionen",
    UiTextKey.HelpCautionTitle to "Hinweise",
    UiTextKey.HelpCurrentFeatures to "Aktuelle Funktionen:\n" +
            "  • Schnellübersetzung: Spracherkennung dann Übersetzung\n" +
            "  • Live-Gespräch: bidirektionale Sprachübersetzung\n" +
            "  • Verlauf: Übersetzungsverlauf einsehen\n" +
            "  • Lernmaterial: Vokabeln und Quiz generieren\n\n" +
            "Übersetzung:\n" +
            "  • Azure AI Spracherkennung\n" +
            "  • Azure Übersetzungsdienst\n",
    UiTextKey.HelpCaution to "Hinweise:\n" +
            "  • Spracherkennung benötigt Internetverbindung\n" +
            "  • Lokaler Übersetzungscache ist offline verfügbar\n" +
            "  • Überprüfen Sie wichtige Übersetzungen professionell\n\n" +
            "Konto und Daten:\n" +
            "  • Verlauf, Lernen und Münzen erfordern Anmeldung\n" +
            "  • Daten werden sicher auf Firebase Firestore gespeichert\n\n" +
            "Fehlerbehebung:\n" +
            "  • Wenn nichts funktioniert, starten Sie die App neu\n",
    UiTextKey.HelpNotesTitle to "Tipps",
    UiTextKey.HelpNotes to "💡 Nutzungstipps:\n\n" +
            "Für bessere Übersetzungen:\n" +
            "  • Sprechen Sie klar und in moderatem Tempo\n" +
            "  • Reduzieren Sie Umgebungsgeräusche für bessere Erkennung\n" +
            "  • Schnellübersetzung eignet sich für kurze Sätze\n\n" +
            "App-Sprache:\n" +
            "  • Standardsprache: Englisch, weitere per KI\n" +
            "  • Die kantonesische Version ist manuell übersetzt\n" +
            "Updates und Feedback:\n" +
            "  • App-Version in Einstellungen → Über\n" +
            "  • Feedback senden in Einstellungen → Feedback\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Feedback",
    UiTextKey.FeedbackDesc to "Vielen Dank für Ihr Feedback! Teilen Sie uns Vorschläge, Fehler oder Bewertungen mit.",
    UiTextKey.FeedbackMessagePlaceholder to "Feedback eingeben...",
    UiTextKey.FeedbackSubmitButton to "Absenden",
    UiTextKey.FeedbackSubmitting to "Wird gesendet...",
    UiTextKey.FeedbackSuccessTitle to "Vielen Dank!",
    UiTextKey.FeedbackSuccessMessage to "Ihr Feedback wurde erfolgreich gesendet. Vielen Dank!",
    UiTextKey.FeedbackErrorTitle to "Senden fehlgeschlagen",
    UiTextKey.FeedbackErrorMessage to "Senden fehlgeschlagen. Überprüfen Sie Ihre Verbindung und versuchen Sie es erneut.",
    UiTextKey.FeedbackMessageRequired to "Bitte geben Sie Ihr Feedback ein.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Live-Gespräch",
    UiTextKey.ContinuousStartButton to "Gespräch starten",
    UiTextKey.ContinuousStopButton to "Aufnahme stoppen",
    UiTextKey.ContinuousStartScreenButton to "Live-Gespräch",
    UiTextKey.ContinuousPersonALabel to "A spricht",
    UiTextKey.ContinuousPersonBLabel to "B spricht",
    UiTextKey.ContinuousCurrentStringLabel to "Aktueller Text:",
    UiTextKey.ContinuousSpeakerAName to "Person A",
    UiTextKey.ContinuousSpeakerBName to "Person B",
    UiTextKey.ContinuousTranslationSuffix to " · Übersetzung",
    UiTextKey.ContinuousPreparingMicText to "Mikrofon wird vorbereitet...(nicht sprechen)",
    UiTextKey.ContinuousTranslatingText to "Übersetze...",

    // History
    UiTextKey.HistoryTitle to "Verlauf",
    UiTextKey.HistoryTabDiscrete to "Schnellübersetzung",
    UiTextKey.HistoryTabContinuous to "Live-Gespräch",
    UiTextKey.HistoryNoContinuousSessions to "Keine Gesprächssitzungen.",
    UiTextKey.HistoryNoDiscreteRecords to "Keine Übersetzungseinträge.",
    UiTextKey.DialogDeleteRecordTitle to "Eintrag löschen?",
    UiTextKey.DialogDeleteRecordMessage to "Diese Aktion kann nicht rückgängig gemacht werden.",
    UiTextKey.DialogDeleteSessionTitle to "Gespräch löschen?",
    UiTextKey.DialogDeleteSessionMessage to "Alle Einträge dieses Gesprächs werden gelöscht. Nicht rückgängig zu machen.",
    UiTextKey.HistoryDeleteSessionButton to "Löschen",
    UiTextKey.HistoryNameSessionTitle to "Benennen",
    UiTextKey.HistorySessionNameLabel to "Gesprächsname",
    UiTextKey.HistorySessionTitleTemplate to "Gespräch {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} Einträge",

    // Filter
    UiTextKey.FilterDropdownDefault to "Alle Sprachen",
    UiTextKey.FilterTitle to "Verlauf filtern",
    UiTextKey.FilterLangDrop to "Sprache",
    UiTextKey.FilterKeyword to "Stichwort",
    UiTextKey.FilterApply to "Anwenden",
    UiTextKey.FilterCancel to "Abbrechen",
    UiTextKey.FilterClear to "Löschen",
    UiTextKey.FilterHistoryScreenTitle to "Filter",

    // Auth
    UiTextKey.AuthLoginTitle to "Anmelden",
    UiTextKey.AuthRegisterTitle to "Registrieren (pausiert)",
    UiTextKey.AuthLoginHint to "Verwenden Sie Ihre registrierte E-Mail und Ihr Passwort.",
    UiTextKey.AuthRegisterRules to "Die Registrierung ist während der Entwicklung pausiert.\nHinweis: Eine nicht existierende E-Mail verhindert die Passwortzurücksetzung.\n" +
            "Registrierungsregeln:\n" +
            "• Gültiges E-Mail-Format (z.B. name@beispiel.de)\n" +
            "• Passwort mindestens 8 Zeichen\n" +
            "• Passwortbestätigung muss übereinstimmen",
    UiTextKey.AuthEmailLabel to "E-Mail",
    UiTextKey.AuthPasswordLabel to "Passwort",
    UiTextKey.AuthConfirmPasswordLabel to "Passwort bestätigen",
        UiTextKey.AuthLoginButton to "Anmelden",
        UiTextKey.AuthGoogleSignInButton to "Mit Google anmelden",
    UiTextKey.AuthRegisterButton to "Registrieren",
    UiTextKey.AuthToggleToRegister to "Kein Konto? Registrieren (pausiert)",
    UiTextKey.AuthToggleToLogin to "Bereits ein Konto? Anmelden",
    UiTextKey.AuthErrorPasswordsMismatch to "Passwörter stimmen nicht überein.",
    UiTextKey.AuthErrorPasswordTooShort to "Passwort muss mindestens 8 Zeichen lang sein.",
    UiTextKey.AuthRegistrationDisabled to "Registrierung ist während der Entwicklung pausiert.",
    UiTextKey.AuthResetEmailSent to "Zurücksetzungs-E-Mail gesendet (falls vorhanden). Überprüfen Sie Ihren Posteingang.",

    // Password reset
    UiTextKey.ForgotPwText to "Passwort vergessen?",
    UiTextKey.ResetPwTitle to "Passwort zurücksetzen",
    UiTextKey.ResetPwText to "Geben Sie die E-Mail Ihres Kontos ein. Wir senden einen Zurücksetzungslink.\nStellen Sie sicher, dass es die registrierte E-Mail ist.\n",
    UiTextKey.ResetSendingText to "Wird gesendet...",
    UiTextKey.ResetSendText to "Zurücksetzungs-E-Mail senden",

    // Settings
    UiTextKey.SettingsTitle to "Einstellungen",
    UiTextKey.SettingsPrimaryLanguageTitle to "Hauptsprache",
    UiTextKey.SettingsPrimaryLanguageDesc to "Wird für Lernerklärungen und -vorschläge verwendet",
    UiTextKey.SettingsPrimaryLanguageLabel to "Hauptsprache",
    UiTextKey.SettingsFontSizeTitle to "Schriftgröße",
    UiTextKey.SettingsFontSizeDesc to "Textgröße für bessere Lesbarkeit anpassen (geräteübergreifend synchronisiert)",
    UiTextKey.SettingsScaleTemplate to "Größe: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Farbthema",
    UiTextKey.SettingsColorPaletteDesc to "Wählen Sie Ihr Farbthema, je 10 Münzen",
    UiTextKey.SettingsColorCostTemplate to "{cost} Münzen",
    UiTextKey.SettingsColorUnlockButton to "Freischalten",
    UiTextKey.SettingsColorSelectButton to "Auswählen",
    UiTextKey.SettingsColorAlreadyUnlocked to "Freigeschaltet",
    UiTextKey.SettingsPreviewHeadline to "Überschrift: Großer Text Vorschau",
    UiTextKey.SettingsPreviewBody to "Fließtext: Normaler Text Vorschau",
    UiTextKey.SettingsPreviewLabel to "Beschriftung: Kleiner Text Vorschau",
    UiTextKey.SettingsAboutTitle to "Über",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Angemeldet, Einstellungen werden automatisch gespeichert und synchronisiert.",
    UiTextKey.SettingsThemeTitle to "Design",
    UiTextKey.SettingsThemeDesc to "Erscheinungsbild wählen: System, Hell, Dunkel oder Geplant.",
    UiTextKey.SettingsThemeSystem to "System",
    UiTextKey.SettingsThemeLight to "Hell",
    UiTextKey.SettingsThemeDark to "Dunkel",
    UiTextKey.SettingsThemeScheduled to "Geplant",
    UiTextKey.SettingsResetPW to "Passwort zurücksetzen",
    UiTextKey.SettingsQuickLinks to "Detaillierte Einstellungen",
    UiTextKey.SettingsNotLoggedInWarning to "Melden Sie sich an, um die Kontoeinstellungen zu sehen. Die App-Sprache können Sie weiterhin ändern.",
    UiTextKey.SettingsVoiceTitle to "Stimmeinstellungen",
    UiTextKey.SettingsVoiceDesc to "Wählen Sie die Vorlesestimme für jede Sprache.",
    UiTextKey.SettingsVoiceLanguageLabel to "Sprache",
    UiTextKey.SettingsVoiceSelectLabel to "Stimme",
    UiTextKey.SettingsVoiceDefault to "Standard",

    // Learning
    UiTextKey.LearningTitle to "Lernen",
    UiTextKey.LearningHintCount to "(*) Anzahl = Übersetzungen mit dieser Sprache.",
    UiTextKey.LearningErrorTemplate to "Fehler: %s",
    UiTextKey.LearningGenerate to "Generieren",
    UiTextKey.LearningRegenerate to "Neu generieren",
    UiTextKey.LearningGenerating to "Wird generiert...",
    UiTextKey.LearningOpenSheetTemplate to "Blatt für {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Lernblatt {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Hauptsprache: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Aktuelle Einträge: {nowCount} (bei Erstellung: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Kein Blattinhalt.",
    UiTextKey.LearningSheetRegenerate to "Neu generieren",
    UiTextKey.LearningSheetGenerating to "Wird generiert...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Was ist das?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Aus Ihrem Übersetzungsverlauf generiertes Lernblatt. Enthält Vokabeln, Definitionen, Beispiele und Grammatikhinweise. Testen Sie Ihr Wissen mit dem Quiz unten!",
    UiTextKey.LearningRegenBlockedTitle to "Momentan nicht regenerierbar",
    UiTextKey.LearningRegenBlockedMessage to "Zur Regenerierung werden mindestens 5 zusätzliche Einträge benötigt. Noch {needed} nötig.",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Noch {needed} Einträge für Regenerierung nötig (min. 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Die Anzahl muss die der letzten Generierung übersteigen",
    UiTextKey.LearningRegenInfoTitle to "Regenerierungsregeln",
    UiTextKey.LearningRegenInfoMessage to "Lernmaterial regenerieren:\n\n• Erste Generierung: jederzeit\n• Regenerierung: mindestens 5 zusätzliche Einträge\n\nDer Button wird blau, wenn genug Einträge vorhanden sind. Wenn er grau ist, übersetzen Sie weiter!\n\n💡 Tipp: Falls die Zählung nicht aktualisiert, starten Sie die App neu.",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Für diese Version wurde bereits ein Quiz erstellt. Generieren Sie ein neues Blatt für ein neues Quiz.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz: {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Quiz generieren",
    UiTextKey.QuizGenerating to "⏳ Generierung...",
    UiTextKey.QuizUpToDate to "✓ Aktuell",
    UiTextKey.QuizBlocked to "🚫 Blockiert",
    UiTextKey.QuizWait to "⏳ Bitte warten...",
    UiTextKey.QuizMaterialsQuizTemplate to "Material: {materials} | Quiz: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Sie können Münzen verdienen!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 {count} mehr für Münzen",
    UiTextKey.QuizCancelButton to "Abbrechen",
    UiTextKey.QuizPreviousButton to "Vorherige Frage",
    UiTextKey.QuizNextButton to "Nächste Frage",
    UiTextKey.QuizSubmitButton to "Absenden",
    UiTextKey.QuizRetakeButton to "Wiederholen",
    UiTextKey.QuizBackButton to "Zurück",
    UiTextKey.QuizLoadingText to "Quiz wird geladen...",
    UiTextKey.QuizGeneratingText to "Quiz wird generiert...",
    UiTextKey.QuizNoMaterialsTitle to "Material nicht gefunden",
    UiTextKey.QuizNoMaterialsMessage to "Generieren Sie zuerst das Lernmaterial, dann greifen Sie auf das Quiz zu.",
    UiTextKey.QuizErrorTitle to "⚠️ Quiz-Fehler",
    UiTextKey.QuizErrorSuggestion to "Vorschlag: Verwenden Sie den Button oben, um ein Quiz zu generieren.",
    UiTextKey.QuizCompletedTitle to "Quiz abgeschlossen!",
    UiTextKey.QuizAnswerReviewTitle to "Antworten überprüfen",
    UiTextKey.QuizYourAnswerTemplate to "Ihre Antwort: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Richtige Antwort: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Frage {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Regenerierung nicht möglich: Material({materials}) < Quiz({quiz}), fügen Sie mehr Übersetzungen hinzu.",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Eine andere Generierung läuft. Bitte warten.",
    UiTextKey.QuizCoinRulesTitle to "🪙 Münzregeln",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ So verdienen:",
    UiTextKey.QuizCoinRulesRequirements to "Anforderungen:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Aktueller Status:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Sie können beim nächsten Quiz Münzen verdienen!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• {count} mehr für Münzen",
    UiTextKey.QuizCoinRule1Coin to "• 1 Münze pro richtige Antwort",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Nur der erste Versuch zählt",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Quiz muss zur Materialversion passen",
    UiTextKey.QuizCoinRulePlus10 to "• Mindestens 10 Einträge mehr als beim letzten bezahlten Quiz",
    UiTextKey.QuizCoinRuleNoDelete to "• Münzen können nach Löschen von Einträgen nicht zurückgewonnen werden",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Material: {count} Einträge",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Quiz: {count} Einträge",
    UiTextKey.QuizCoinRuleGotIt to "Verstanden!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Neues Quiz generieren?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Sie können mit diesem Quiz Münzen verdienen! (nur erster Versuch)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Sie können mit diesem Quiz keine Münzen verdienen.",
    UiTextKey.QuizRegenNeedMoreTemplate to "Sie brauchen {count} weitere Übersetzungen für Münzberechtigung (10 mehr als beim letzten bezahlten Quiz).",
    UiTextKey.QuizRegenReminder to "Tipp: Sie können üben und wiederholen, aber Münzen gibt es nur beim ersten Versuch mit genug Einträgen.",
    UiTextKey.QuizRegenGenerateButton to "Generieren",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Münzen verdient!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Glückwunsch! Sie haben {Coins} Münzen verdient!",
    UiTextKey.QuizCoinsRule1 to "• 1 Münze pro richtige Antwort beim ersten Versuch",
    UiTextKey.QuizCoinsRule2 to "• Keine Münzen für weitere Versuche",
    UiTextKey.QuizCoinsRule3 to "• Neue Quiz benötigen 10 zusätzliche Einträge",
    UiTextKey.QuizCoinsRule4 to "• Quiz muss zur Materialversion passen",
    UiTextKey.QuizCoinsRule5 to "• Gesamtmünzen im Verlauf sichtbar",
    UiTextKey.QuizCoinsGreatButton to "Toll!",
    UiTextKey.QuizOutdatedMessage to "Dieses Quiz basiert auf einem älteren Lernblatt.",
    UiTextKey.QuizRecordsLabel to "Einträge",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Meine Münzen",
    UiTextKey.HistoryCoinRulesTitle to "Münzregeln:",
    UiTextKey.HistoryCoinHowToEarnTitle to "So verdienen:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 Münze pro richtige Antwort",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Nur der erste Versuch pro Version",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Quiz muss zum aktuellen Material passen",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Anti-Betrugsregeln:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Mindestens 10 neue Übersetzungen seit letztem Gewinn",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Quiz-Version muss zum Material passen",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Löschen von Einträgen blockiert Quizregeneration (es sei denn, die Anzahl übersteigt die vorherige)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Keine Münzen für weitere Versuche",
    UiTextKey.HistoryCoinTipsTitle to "💡 Tipps:",
    UiTextKey.HistoryCoinTipsRule1 to "• Fügen Sie regelmäßig Übersetzungen hinzu",
    UiTextKey.HistoryCoinTipsRule2 to "• Lernen Sie gut vor dem ersten Versuch!",
    UiTextKey.HistoryCoinGotItButton to "Verstanden!",

    // History info
    UiTextKey.HistoryInfoTitle to "Verlauf-Info",
    UiTextKey.HistoryInfoLimitMessage to "Der Verlauf zeigt die neuesten {limit} Einträge. Erweitern Sie das Limit im Shop!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Ältere Einträge werden aufbewahrt, aber aus Leistungsgründen ausgeblendet.",
    UiTextKey.HistoryInfoFavoritesMessage to "Um eine Übersetzung dauerhaft zu speichern, tippen Sie im Verlauf auf ❤️ als Favorit.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Gespeichertes finden Sie unter Einstellungen → Favoriten.",
    UiTextKey.HistoryInfoFilterMessage to "Verwenden Sie den Filter, um in den {limit} angezeigten Einträgen zu suchen.",
    UiTextKey.HistoryInfoGotItButton to "Verstanden",

    // Word bank
    UiTextKey.WordBankTitle to "Wortbank",
    UiTextKey.WordBankSelectLanguage to "Wählen Sie eine Sprache, um Ihre Wortbank anzuzeigen oder zu erstellen:",
    UiTextKey.WordBankNoHistory to "Kein Übersetzungsverlauf",
    UiTextKey.WordBankNoHistoryHint to "Beginnen Sie mit dem Übersetzen, um Ihre Wortbank zu erstellen!",
    UiTextKey.WordBankWordsCount to "Wörter",
    UiTextKey.WordBankGenerating to "Wird generiert...",
    UiTextKey.WordBankGenerate to "Wortbank generieren",
    UiTextKey.WordBankRegenerate to "Wortbank regenerieren",
    UiTextKey.WordBankRefresh to "🔄 Bank aktualisieren",
    UiTextKey.WordBankEmpty to "Wortbank leer",
    UiTextKey.WordBankEmptyHint to "Tippen Sie oben, um aus Ihrem Verlauf zu generieren.",
    UiTextKey.WordBankExample to "Beispiel:",
    UiTextKey.WordBankDifficulty to "Schwierigkeit:",
    UiTextKey.WordBankFilterCategory to "Kategorie",
    UiTextKey.WordBankFilterCategoryAll to "Alle Kategorien",
    UiTextKey.WordBankFilterDifficultyLabel to "Schwierigkeit:",
    UiTextKey.WordBankFilterNoResults to "Keine Wörter entsprechen den Filtern",
    UiTextKey.WordBankRefreshAvailable to "✅ Aktualisierung verfügbar!",
    UiTextKey.WordBankRecordsNeeded to "Einträge (20 nötig für Aktualisierung)",
    UiTextKey.WordBankRegenInfoTitle to "Aktualisierungsregeln",
    UiTextKey.WordBankRegenInfoMessage to "Wortbank aktualisieren:\n\n• Erste Generierung: jederzeit\n• Aktualisierung: mindestens 20 zusätzliche Einträge\n\nDer Button wird blau, wenn genug vorhanden. Wenn grau, übersetzen Sie weiter!\n\n💡 Tipp: Falls die Zählung nicht aktualisiert, starten Sie die App neu.",
    UiTextKey.WordBankHistoryCountTemplate to "Aktuelle Einträge: {nowCount} (bei Erstellung: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Abmelden?",
    UiTextKey.DialogLogoutMessage to "Übersetzung und Verlaufszugriff erfordern erneute Anmeldung.",
    UiTextKey.DialogGenerateOverwriteTitle to "Material überschreiben?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Vorhandenes Material wird überschrieben.\nMaterial für {speclanguage} generieren?",

    // Profile
    UiTextKey.ProfileTitle to "Profil",
    UiTextKey.ProfileUsernameLabel to "Benutzername",
    UiTextKey.ProfileUsernameHint to "Benutzernamen eingeben",
    UiTextKey.ProfileUpdateButton to "Profil aktualisieren",
    UiTextKey.ProfileUpdateSuccess to "Profil aktualisiert",
    UiTextKey.ProfileUpdateError to "Aktualisierung fehlgeschlagen",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Konto löschen",
    UiTextKey.AccountDeleteWarning to "⚠️ Diese Aktion ist dauerhaft und nicht rückgängig zu machen!",
    UiTextKey.AccountDeleteConfirmMessage to "Alle Daten werden dauerhaft gelöscht: Verlauf, Wortbank, Lernmaterial, Einstellungen. Geben Sie Ihr Passwort zur Bestätigung ein.",
    UiTextKey.AccountDeletePasswordLabel to "Passwort",
    UiTextKey.AccountDeleteButton to "Konto löschen",
    UiTextKey.AccountDeleteSuccess to "Konto erfolgreich gelöscht",
    UiTextKey.AccountDeleteError to "Löschung fehlgeschlagen",
    UiTextKey.AccountDeleteReauthRequired to "Geben Sie Ihr Passwort zur Bestätigung der Löschung ein",

    // Favorites
    UiTextKey.FavoritesTitle to "Favoriten",
    UiTextKey.FavoritesEmpty to "Keine Favoriten",
    UiTextKey.FavoritesAddSuccess to "Zu Favoriten hinzugefügt",
    UiTextKey.FavoritesRemoveSuccess to "Aus Favoriten entfernt",
    UiTextKey.FavoritesAddButton to "Zu Favoriten",
    UiTextKey.FavoritesRemoveButton to "Aus Favoriten entfernen",
    UiTextKey.FavoritesNoteLabel to "Notiz",
    UiTextKey.FavoritesNoteHint to "Notiz hinzufügen (optional)",
    UiTextKey.FavoritesTabRecords to "Einträge",
    UiTextKey.FavoritesTabSessions to "Gespräche",
    UiTextKey.FavoritesSessionsEmpty to "Keine gespeicherten Gespräche",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} Nachrichten",

    // Custom words
    UiTextKey.CustomWordsTitle to "Eigene Wörter",
    UiTextKey.CustomWordsAdd to "Wort hinzufügen",
    UiTextKey.CustomWordsEdit to "Wort bearbeiten",
    UiTextKey.CustomWordsDelete to "Wort löschen",
    UiTextKey.CustomWordsOriginalLabel to "Originalwort",
    UiTextKey.CustomWordsTranslatedLabel to "Übersetzung",
    UiTextKey.CustomWordsPronunciationLabel to "Aussprache (optional)",
    UiTextKey.CustomWordsExampleLabel to "Beispiel (optional)",
    UiTextKey.CustomWordsSaveSuccess to "Wort gespeichert",
    UiTextKey.CustomWordsDeleteSuccess to "Wort gelöscht",
    UiTextKey.CustomWordsAlreadyExists to "Dieses Wort existiert bereits",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Originalsprache",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Übersetzungssprache",
    UiTextKey.CustomWordsSaveButton to "Speichern",
    UiTextKey.CustomWordsCancelButton to "Abbrechen",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Auto-Erkennung",
    UiTextKey.LanguageDetectDetecting to "Erkennung...",
    UiTextKey.LanguageDetectedTemplate to "Erkannt: {language}",
    UiTextKey.LanguageDetectFailed to "Erkennung fehlgeschlagen",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Text aus Bild scannen",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Hinweis: Bildtexterkennung ist möglicherweise nicht vollständig genau. Überprüfen Sie den erkannten Text." +
            "Unterstützt Lateinisch (Englisch usw.), Chinesisch, Japanisch und Koreanisch.",
    UiTextKey.ImageRecognitionScanning to "Text wird gescannt...",
    UiTextKey.ImageRecognitionSuccess to "Text erfolgreich erkannt",

    // Cache
    UiTextKey.CacheClearButton to "Cache leeren",
    UiTextKey.CacheClearSuccess to "Cache geleert",
    UiTextKey.CacheStatsTemplate to "Cache: {count} gespeicherte Übersetzungen",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Automatisches Design",
    UiTextKey.SettingsAutoThemeDesc to "Automatisch zwischen Hell und Dunkel nach Uhrzeit wechseln",
    UiTextKey.SettingsAutoThemeEnabled to "Aktiviert",
    UiTextKey.SettingsAutoThemeDisabled to "Deaktiviert",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Dunkelmodus ab:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Hellmodus ab:",
    UiTextKey.SettingsAutoThemePreview to "Das Design ändert sich automatisch zu den festgelegten Zeiten",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Offline-Modus",
    UiTextKey.OfflineModeMessage to "Sie sind derzeit offline. Zwischengespeicherte Daten werden angezeigt.",
    UiTextKey.OfflineModeRetry to "Verbindung erneut versuchen",
    UiTextKey.OfflineDataCached to "Zwischengespeicherte Daten verfügbar",
    UiTextKey.OfflineSyncPending to "Änderungen werden online synchronisiert",

    // Image capture
    UiTextKey.ImageSourceTitle to "Bildquelle wählen",
    UiTextKey.ImageSourceCamera to "Foto aufnehmen",
    UiTextKey.ImageSourceGallery to "Aus Galerie wählen",
    UiTextKey.ImageSourceCancel to "Abbrechen",
    UiTextKey.CameraCaptureContentDesc to "Aufnehmen",

    // Friends
    UiTextKey.FriendsTitle to "Freunde",
    UiTextKey.FriendsMenuButton to "Freunde",
    UiTextKey.FriendsAddButton to "Freund hinzufügen",
    UiTextKey.FriendsSearchTitle to "Benutzer suchen",
    UiTextKey.FriendsSearchPlaceholder to "Benutzername oder ID...",
    UiTextKey.FriendsSearchMinChars to "Mindestens 2 Zeichen eingeben",
    UiTextKey.FriendsSearchNoResults to "Kein Benutzer gefunden",
    UiTextKey.FriendsListEmpty to "Fügen Sie Freunde hinzu, um zu chatten und Lernmaterial zu teilen.",
    UiTextKey.FriendsRequestsSection to "Freundschaftsanfragen ({count})",
    UiTextKey.FriendsSectionTitle to "Freunde ({count})",
    UiTextKey.FriendsAcceptButton to "Annehmen",
    UiTextKey.FriendsRejectButton to "Ablehnen",
    UiTextKey.FriendsRemoveButton to "Entfernen",
    UiTextKey.FriendsRemoveDialogTitle to "Freund entfernen",
    UiTextKey.FriendsRemoveDialogMessage to "{username} aus Ihrer Freundesliste entfernen?",
    UiTextKey.FriendsSendRequestButton to "Hinzufügen",
    UiTextKey.FriendsRequestSentSuccess to "Freundschaftsanfrage gesendet!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Anfrage angenommen!",
    UiTextKey.FriendsRequestRejectedSuccess to "Anfrage abgelehnt",
    UiTextKey.FriendsRemovedSuccess to "Freund entfernt",
    UiTextKey.FriendsRequestFailed to "Senden fehlgeschlagen",
    UiTextKey.FriendsCloseButton to "Schließen",
    UiTextKey.FriendsCancelButton to "Abbrechen",
    UiTextKey.FriendsRemoveConfirm to "Entfernen",
    UiTextKey.FriendsNewRequestsTemplate to "Sie haben {count} neue Freundschaftsanfrage(n)!",
    UiTextKey.FriendsSentRequestsSection to "Gesendete Anfragen ({count})",
    UiTextKey.FriendsPendingStatus to "Ausstehend",
    UiTextKey.FriendsCancelRequestButton to "Anfrage zurückziehen",
    UiTextKey.FriendsUnreadMessageDesc to "Nachricht senden",
    UiTextKey.FriendsDeleteModeButton to "Freunde löschen",
    UiTextKey.FriendsDeleteSelectedButton to "Ausgewählte löschen",
    UiTextKey.FriendsDeleteMultipleTitle to "Freunde löschen",
    UiTextKey.FriendsDeleteMultipleMessage to "Die {count} ausgewählten Freunde löschen?",
    UiTextKey.FriendsSearchMinChars3 to "Mindestens 3 Zeichen für Namen eingeben",
    UiTextKey.FriendsSearchByUserIdHint to "Oder exakt nach Benutzer-ID suchen",
    UiTextKey.FriendsStatusAlreadyFriends to "Bereits befreundet",
    UiTextKey.FriendsStatusRequestSent to "Anfrage gesendet — wartet auf Antwort",
    UiTextKey.FriendsStatusRequestReceived to "Dieser Benutzer hat Ihnen eine Anfrage gesendet",

    // Chat
    UiTextKey.ChatTitle to "Chat mit {username}",
    UiTextKey.ChatInputPlaceholder to "Nachricht eingeben...",
    UiTextKey.ChatSendButton to "Senden",
    UiTextKey.ChatEmpty to "Keine Nachrichten. Starten Sie ein Gespräch!",
    UiTextKey.ChatMessageSent to "Nachricht gesendet",
    UiTextKey.ChatMessageFailed to "Senden fehlgeschlagen",
    UiTextKey.ChatMarkingRead to "Wird markiert...",
    UiTextKey.ChatLoadingMessages to "Nachrichten werden geladen...",
    UiTextKey.ChatToday to "Heute",
    UiTextKey.ChatYesterday to "Gestern",
    UiTextKey.ChatUnreadBadge to "{count} ungelesen",
    UiTextKey.ChatTranslateButton to "Übersetzen",
    UiTextKey.ChatTranslateDialogTitle to "Gespräch übersetzen",
    UiTextKey.ChatTranslateDialogMessage to "Nachrichten Ihres Freundes in Ihre Sprache übersetzen? Die Sprache jeder Nachricht wird erkannt und übersetzt.",
    UiTextKey.ChatTranslateConfirm to "Alle übersetzen",
    UiTextKey.ChatTranslating to "Nachrichten werden übersetzt...",
    UiTextKey.ChatTranslated to "Nachrichten übersetzt",
    UiTextKey.ChatShowOriginal to "Original anzeigen",
    UiTextKey.ChatShowTranslation to "Übersetzung anzeigen",
    UiTextKey.ChatTranslateFailed to "Übersetzung fehlgeschlagen",
    UiTextKey.ChatTranslatedLabel to "Übersetzt",

    // Sharing
    UiTextKey.ShareTitle to "Teilen",
    UiTextKey.ShareInboxTitle to "Geteilte Inbox",
    UiTextKey.ShareInboxEmpty to "Kein Inhalt geteilt. Ihre Freunde können Wörter und Material teilen!",
    UiTextKey.ShareWordButton to "Wort teilen",
    UiTextKey.ShareMaterialButton to "Material teilen",
    UiTextKey.ShareSelectFriendTitle to "Freund wählen",
    UiTextKey.ShareSelectFriendMessage to "Wählen Sie einen Freund zum Teilen:",
    UiTextKey.ShareSuccess to "Erfolgreich geteilt!",
    UiTextKey.ShareFailed to "Teilen fehlgeschlagen",
    UiTextKey.ShareWordWith to "Wort mit {username} teilen",
    UiTextKey.ShareMaterialWith to "Material mit {username} teilen",
    UiTextKey.ShareAcceptButton to "Annehmen",
    UiTextKey.ShareDismissButton to "Verwerfen",
    UiTextKey.ShareAccepted to "Zu Ihrer Sammlung hinzugefügt",
    UiTextKey.ShareDismissed to "Element verworfen",
    UiTextKey.ShareActionFailed to "Aktion fehlgeschlagen",
    UiTextKey.ShareTypeWord to "Wort",
    UiTextKey.ShareTypeLearningSheet to "Lernblatt",
    UiTextKey.ShareReceivedFrom to "Von: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} neue(s) Element(e)!",
    UiTextKey.ShareViewFullMaterial to "Tippen Sie auf „Ansehen“ für das vollständige Material",
    UiTextKey.ShareDeleteItemTitle to "Element löschen",
    UiTextKey.ShareDeleteItemMessage to "Dieses geteilte Element löschen? Nicht rückgängig zu machen.",
    UiTextKey.ShareDeleteButton to "Löschen",
    UiTextKey.ShareViewButton to "Ansehen",
    UiTextKey.ShareItemNotFound to "Element nicht gefunden.",
    UiTextKey.ShareNoContent to "Kein Inhalt in diesem Material.",
    UiTextKey.ShareSaveToSelf to "In meiner Box speichern",
    UiTextKey.ShareSavedToSelf to "In Ihrer Box gespeichert!",

    // My profile
    UiTextKey.MyProfileTitle to "Mein Profil",
    UiTextKey.MyProfileUserId to "Benutzer-ID",
    UiTextKey.MyProfileUsername to "Benutzername",
    UiTextKey.MyProfileDisplayName to "Anzeigename",
    UiTextKey.MyProfileCopyUserId to "ID kopieren",
    UiTextKey.MyProfileCopyUsername to "Namen kopieren",
    UiTextKey.MyProfileShare to "Profil teilen",
    UiTextKey.MyProfileCopied to "In Zwischenablage kopiert!",
    UiTextKey.MyProfileLanguages to "Sprachen",
    UiTextKey.MyProfilePrimaryLanguage to "Hauptsprache",
    UiTextKey.MyProfileLearningLanguages to "Lernsprachen",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Freundeseite",
    UiTextKey.FriendsInfoMessage to "• Nach unten ziehen, um Liste, Anfragen und Status zu aktualisieren.\n" +
            "• Karte antippen, um den Chat zu öffnen.\n" +
            "• Roter Punkt (●) zeigt ungelesene Nachrichten, ✓✓ zum Lesen markieren.\n" +
            "• 📥 für die geteilte Inbox, ✓✓ zum Punkt löschen.\n" +
            "• 🚫 zum Blockieren — Freund wird entfernt und kann Sie nicht kontaktieren.\n" +
            "• Blockieren löscht auch den Chatverlauf.\n" +
            "• Papierkorb-Symbol für Löschmodus.\n" +
            "• Freund entfernen löscht auch alle Nachrichten.\n" +
            "• Suchsymbol zum Suchen nach Name oder ID.\n" +
            "• Push-Benachrichtigungen sind standardmäßig deaktiviert — in Einstellungen aktivieren.\n",
    UiTextKey.FriendsEmptyTitle to "Keine Freunde",
    UiTextKey.FriendsEmptyMessage to "Tippen Sie auf „Freund hinzufügen“, um nach Name oder ID zu suchen.\n",
    UiTextKey.FriendsInfoGotItButton to "Verstanden",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Lernseite",
    UiTextKey.LearningInfoMessage to "• Ziehen zum Aktualisieren der Einträge.\n" +
            "• Jede Karte zeigt Sprache und Anzahl.\n" +
            "• „Generieren“ für ein Lernblatt (1. Mal kostenlos).\n" +
            "• Regenerierung erfordert mind. 5 zusätzliche Einträge.\n" +
            "• Blatt-Button öffnet generiertes Material.\n" +
            "• Nach dem Blatt können Sie auch das Quiz machen.",
    UiTextKey.LearningEmptyTitle to "Kein Übersetzungsverlauf",
    UiTextKey.LearningEmptyMessage to "Beginnen Sie mit dem Übersetzen, um Einträge zu erstellen.\n" +
            "Blätter werden aus dem Verlauf generiert.\n" +
            "Nach dem Übersetzen nach unten ziehen zum Aktualisieren.",
    UiTextKey.LearningInfoGotItButton to "Verstanden",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Wortbank-Seite",
    UiTextKey.WordBankInfoMessage to "• Ziehen zum Aktualisieren der Sprachliste.\n" +
            "• Sprache wählen zum Anzeigen oder Erstellen.\n" +
            "• Die Wortbank wird aus dem Verlauf generiert.\n" +
            "• Aktualisierung erfordert mind. 20 zusätzliche Einträge.\n" +
            "• Eigene Wörter manuell hinzufügen.\n" +
            "• Wörter mit Freunden teilen.",
    UiTextKey.WordBankInfoGotItButton to "Verstanden",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Geteilte Inbox",
    UiTextKey.ShareInboxInfoMessage to "• Ziehen zum Aktualisieren der Inbox.\n" +
            "• Von Freunden geteilte Elemente erscheinen hier.\n" +
            "• Wörter annehmen oder verwerfen.\n" +
            "• „Ansehen“ für Blätter und Quiz.\n" +
            "• Roter Punkt (●) zeigt neue/ungelesene Elemente.\n" +
            "• Bestätigung vor dem Verwerfen eines geteilten Worts.",
    UiTextKey.ShareInboxInfoGotItButton to "Verstanden",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Profilsichtbarkeit",
    UiTextKey.MyProfileVisibilityPublic to "Öffentlich",
    UiTextKey.MyProfileVisibilityPrivate to "Privat",
    UiTextKey.MyProfileVisibilityDescription to "Öffentlich: Jeder kann Sie finden und hinzufügen.\nPrivat: Sie erscheinen nicht in der Suche.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Wort verwerfen",
    UiTextKey.ShareDismissWordMessage to "Dieses geteilte Wort verwerfen? Nicht rückgängig zu machen.",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Sprache: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Schließen",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Verbunden oder ausstehend",
    UiTextKey.AccessibilityNewMessages to "Neue Nachrichten",
    UiTextKey.AccessibilityNewReleasesIcon to "Neue Elemente Anzeige",
    UiTextKey.AccessibilitySuccessIcon to "Erfolg",
    UiTextKey.AccessibilityErrorIcon to "Fehler",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Art des geteilten Elements",
    UiTextKey.AccessibilityAddCustomWords to "Eigene Wörter hinzufügen",
    UiTextKey.AccessibilityWordBankExists to "Wortbank vorhanden",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.Feedback",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Benachr. Einst.",
    UiTextKey.FriendsNotifSettingsTitle to "Benachrichtigungseinstellungen",
    UiTextKey.FriendsNotifNewMessages to "Neue Chat-Nachrichten",
    UiTextKey.FriendsNotifFriendRequests to "Empfangene Freundschaftsanfragen",
    UiTextKey.FriendsNotifRequestAccepted to "Freundschaftsanfrage angenommen",
    UiTextKey.FriendsNotifSharedInbox to "Neue geteilte Elemente",
    UiTextKey.FriendsNotifCloseButton to "Fertig",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "In-App-Badges (rote Punkte)",
    UiTextKey.InAppBadgeMessages to "Badge für ungelesene Nachrichten",
    UiTextKey.InAppBadgeFriendRequests to "Badge für Freundschaftsanfragen",
    UiTextKey.InAppBadgeSharedInbox to "Badge für ungelesene geteilte Inbox",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Bitte melden Sie sich an, um fortzufahren.",
    UiTextKey.ErrorSaveFailedRetry to "Speichern fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorLoadFailedRetry to "Laden fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorNetworkRetry to "Netzwerkfehler. Überprüfen Sie Ihre Verbindung.",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Noch {needed} Übersetzungen für Materialerstellung nötig",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Zum Live-Gespräch wechseln →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Chat löschen",
    UiTextKey.ChatClearConversationTitle to "Gespräch löschen",
    UiTextKey.ChatClearConversationMessage to "Alle Nachrichten in diesem Gespräch ausblenden? Sie bleiben auch nach erneutem Öffnen ausgeblendet. Die andere Person ist nicht betroffen.",
    UiTextKey.ChatClearConversationConfirm to "Alles löschen",
    UiTextKey.ChatClearConversationSuccess to "Gespräch gelöscht",

    // Block user
    UiTextKey.BlockUserButton to "Blockieren",
    UiTextKey.BlockUserTitle to "Diesen Benutzer blockieren?",
    UiTextKey.BlockUserMessage to "{username} blockieren? Wird aus Ihrer Liste entfernt und kann Sie nicht mehr kontaktieren.",
    UiTextKey.BlockUserConfirm to "Blockieren",
    UiTextKey.BlockUserSuccess to "Benutzer blockiert und aus Freundesliste entfernt.",
    UiTextKey.BlockedUsersTitle to "Blockierte Benutzer",
    UiTextKey.BlockedUsersEmpty to "Keine blockierten Benutzer.",
    UiTextKey.UnblockUserButton to "Entsperren",
    UiTextKey.UnblockUserTitle to "Entsperren?",
    UiTextKey.UnblockUserMessage to "{username} entsperren? Kann Ihnen wieder Anfragen senden.",
    UiTextKey.UnblockUserSuccess to "Benutzer entsperrt.",
    UiTextKey.BlockedUsersManageButton to "Blockierte verwalten",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Anfragenotiz (optional)",
    UiTextKey.FriendsRequestNotePlaceholder to "Kurze Notiz hinzufügen...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Lernblatt fertig! Tippen zum Öffnen.",
    UiTextKey.GenerationBannerWordBank to "Wortbank fertig! Tippen zum Anzeigen.",
    UiTextKey.GenerationBannerQuiz to "Quiz fertig! Tippen zum Starten.",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Benachrichtigungen",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Chinesisch (traditionell)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Freundesystem",
    UiTextKey.HelpFriendSystemBody to "• Freunde nach Name oder ID suchen\n" +
            "• Anfragen senden, annehmen oder ablehnen\n" +
            "• Echtzeit-Chat mit Übersetzung\n" +
            "• Wörter und Lernmaterial teilen\n" +
            "• Geteiltes in der Inbox verwalten\n" +
            "• Roter Punkt (●) zeigt ungelesene oder neue Inhalte\n" +
            "• Nach unten ziehen zum Aktualisieren",
    UiTextKey.HelpProfileVisibilityTitle to "Profilsichtbarkeit",
    UiTextKey.HelpProfileVisibilityBody to "• Profil öffentlich oder privat in Einstellungen\n" +
            "• Öffentlich: Jeder kann Sie finden\n" +
            "• Privat: Unsichtbar in der Suche\n" +
            "• Auch privat: ID teilen zum Hinzufügen",
    UiTextKey.HelpColorPalettesTitle to "Themen und Münzen",
    UiTextKey.HelpColorPalettesBody to "• 1 kostenloses Thema: Himmelblau (Standard)\n" +
            "• 10 freischaltbare Themen, je 10 Münzen\n" +
            "• Münzen durch Quiz verdienen\n" +
            "• Münzen für Themen und Verlaufserweiterung\n" +
            "• Auto-Thema: Hell 6–18 Uhr, Dunkel 18–6 Uhr",
    UiTextKey.HelpPrivacyTitle to "Datenschutz und Daten",
    UiTextKey.HelpPrivacyBody to "• Audio nur für Erkennung, keine dauerhafte Speicherung\n" +
            "• OCR wird auf dem Gerät verarbeitet (Datenschutz)\n" +
            "• Konto und alle Daten jederzeit löschbar\n" +
            "• Privater Modus: nicht in der Suche sichtbar\n" +
            "• Alle Daten werden sicher über Firebase synchronisiert",
    UiTextKey.HelpAppVersionTitle to "App-Version",
    UiTextKey.HelpAppVersionNotes to "• Verlaufslimit: 30 bis 60 Einträge (erweiterbar mit Münzen)\n" +
            "• Benutzername ist eindeutig — Änderung gibt den alten frei\n" +
            "• Automatische Abmeldung bei Sicherheitsupdates\n" +
            "• Alle Übersetzungen durch Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Sofortübersetzung",
    UiTextKey.OnboardingPage1Desc to "Schnellübersetzung für kurze Sätze, Live-Gespräch für bidirektionalen Dialog.",
    UiTextKey.OnboardingPage2Title to "Vokabeln lernen",
    UiTextKey.OnboardingPage2Desc to "Vokabelblätter und Quiz aus Ihrem Verlauf generieren.",
    UiTextKey.OnboardingPage3Title to "Mit Freunden vernetzen",
    UiTextKey.OnboardingPage3Desc to "Chatten, Wörter teilen und gemeinsam lernen.",
    UiTextKey.OnboardingSkipButton to "Überspringen",
    UiTextKey.OnboardingNextButton to "Weiter",
    UiTextKey.OnboardingGetStartedButton to "Los geht's",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Willkommen zurück, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Benutzername:",
    UiTextKey.ChatUserIdLabel to "Benutzer-ID:",
    UiTextKey.ChatLearningLabel to "Lernt:",
    UiTextKey.ChatBlockedMessage to "Sie können diesem Benutzer keine Nachricht senden.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Suchen",
    UiTextKey.CustomWordsEmptyState to "Keine eigenen Wörter",
    UiTextKey.CustomWordsEmptyHint to "Tippen Sie auf +, um ein Wort hinzuzufügen",
    UiTextKey.CustomWordsNoSearchResults to "Keine passenden Wörter",
    UiTextKey.AddCustomWordHintTemplate to "Wort auf {from} und Übersetzung auf {to} eingeben",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} Einträge",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "E-Mail: {email}",
    UiTextKey.ProfileUsernameHintFull to "Benutzername für Freunde (3–20 Zeichen, alphanumerisch/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Keine Stimmoptionen für diese Sprache",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "App wurde aktualisiert. Bitte erneut anmelden",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Favoritenlimit erreicht",
    UiTextKey.FavoritesLimitMessage to "Maximal 20 Favoriten. Entfernen Sie einen, um einen neuen hinzuzufügen.",
    UiTextKey.FavoritesLimitGotIt to "Verstanden",
    UiTextKey.FavoritesInfoTitle to "Favoriten-Info",
    UiTextKey.FavoritesInfoMessage to "Maximal 20 Favoriten (Einträge und Gespräche). Dieses Limit reduziert die Datenbankbelastung. Entfernen Sie einen für einen neuen.",
    UiTextKey.FavoritesInfoGotIt to "Verstanden",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Sprachwechsel nicht möglich",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Die Hauptsprache kann nur alle 30 Tage geändert werden. Bitte warten Sie noch {days} Tag(e).",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Die Hauptsprache kann nur alle 30 Tage geändert werden. Bitte warten Sie noch {hours} Stunde(n).",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Sprachwechsel bestätigen",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Die Hauptsprache zu ändern verhindert weitere Änderungen für 30 Tage. Fortfahren?",

    // Bottom navigation
    UiTextKey.NavHome to "Startseite",
    UiTextKey.NavTranslate to "Übersetzen",
    UiTextKey.NavLearn to "Lernen",
    UiTextKey.NavFriends to "Freunde",
    UiTextKey.NavSettings to "Einstellungen",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Kameraberechtigung erforderlich",
    UiTextKey.CameraPermissionMessage to "Bitte erlauben Sie den Kamerazugriff für Texterkennung.",
    UiTextKey.CameraPermissionGrant to "Erlauben",
    UiTextKey.MicPermissionMessage to "Mikrofonzugriff wird für die Spracherkennung benötigt.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Die {count} ausgewählten Elemente löschen? Nicht rückgängig zu machen.",
    UiTextKey.WordBankDeleteConfirm to "„{word}“ löschen?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Alle annehmen",
    UiTextKey.FriendsRejectAllButton to "Alle ablehnen",
    UiTextKey.ChatBlockedCannotSend to "Nachricht kann nicht gesendet werden",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "{name} freischalten?",
    UiTextKey.ShopUnlockCost to "Kosten: {cost} Münzen",
    UiTextKey.ShopYourCoins to "Meine Münzen: {coins}",
    UiTextKey.ShopUnlockButton to "Freischalten",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Hauptsprache",
    UiTextKey.HelpPrimaryLanguageBody to "• Die Hauptsprache wird für Lernerklärungen verwendet\n" +
            "• Nur alle 30 Tage änderbar für Konsistenz\n" +
            "• In den Einstellungen änderbar\n" +
            "• Globale Einstellung für alle Seiten",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Tipp: Für bessere Erkennung stellen Sie „Quellsprache“ auf die Sprache des zu scannenden Textes.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Namensänderung nicht möglich",
    UiTextKey.SettingsUsernameCooldownMessage to "Der Benutzername kann nur alle 30 Tage geändert werden. Bitte warten Sie noch {days} Tag(e).",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Der Benutzername kann nur alle 30 Tage geändert werden. Bitte warten Sie noch {hours} Stunde(n).",
    UiTextKey.SettingsUsernameConfirmTitle to "Namensänderung bestätigen",
    UiTextKey.SettingsUsernameConfirmMessage to "Die Änderung des Benutzernamens verhindert weitere Änderungen für 30 Tage. Fortfahren?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Keine Internetverbindung. Überprüfen Sie Ihre Verbindung.",
    UiTextKey.ErrorPermissionDenied to "Sie haben keine Berechtigung für diese Aktion.",
    UiTextKey.ErrorSessionExpired to "Sitzung abgelaufen. Bitte erneut anmelden.",
    UiTextKey.ErrorItemNotFound to "Element nicht gefunden. Es wurde möglicherweise gelöscht.",
    UiTextKey.ErrorAccessDenied to "Zugriff verweigert.",
    UiTextKey.ErrorAlreadyFriends to "Sie sind bereits mit diesem Benutzer befreundet.",
    UiTextKey.ErrorUserBlocked to "Aktion nicht möglich. Der Benutzer ist möglicherweise blockiert.",
    UiTextKey.ErrorRequestNotFound to "Diese Freundschaftsanfrage existiert nicht mehr.",
    UiTextKey.ErrorRequestAlreadyHandled to "Diese Anfrage wurde bereits bearbeitet.",
    UiTextKey.ErrorNotAuthorized to "Sie sind nicht berechtigt, diese Aktion durchzuführen.",
    UiTextKey.ErrorRateLimited to "Zu viele Anfragen. Bitte versuchen Sie es später erneut.",
    UiTextKey.ErrorInvalidInput to "Ungültige Eingabe. Überprüfen und erneut versuchen.",
    UiTextKey.ErrorOperationNotAllowed to "Diese Operation ist derzeit nicht erlaubt.",
    UiTextKey.ErrorTimeout to "Zeitüberschreitung. Bitte erneut versuchen.",
    UiTextKey.ErrorSendMessageFailed to "Nachricht senden fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorFriendRequestSent to "Freundschaftsanfrage gesendet!",
    UiTextKey.ErrorFriendRequestFailed to "Anfrage senden fehlgeschlagen.",
    UiTextKey.ErrorFriendRemoved to "Freund entfernt.",
    UiTextKey.ErrorFriendRemoveFailed to "Entfernen fehlgeschlagen. Überprüfen Sie Ihre Verbindung.",
    UiTextKey.ErrorBlockSuccess to "Benutzer blockiert.",
    UiTextKey.ErrorBlockFailed to "Blockieren fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorUnblockSuccess to "Benutzer entsperrt.",
    UiTextKey.ErrorUnblockFailed to "Entsperren fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorAcceptRequestSuccess to "Freundschaftsanfrage angenommen!",
    UiTextKey.ErrorAcceptRequestFailed to "Annehmen fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorRejectRequestSuccess to "Freundschaftsanfrage abgelehnt.",
    UiTextKey.ErrorRejectRequestFailed to "Ablehnen fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorOfflineMessage to "Sie sind offline. Einige Funktionen sind möglicherweise nicht verfügbar.",
    UiTextKey.ErrorChatDeletionFailed to "Chat löschen fehlgeschlagen. Bitte erneut versuchen.",
    UiTextKey.ErrorGenericRetry to "Ein Fehler ist aufgetreten. Bitte erneut versuchen.",
)
