package com.example.fyp.model.ui

/**
 * French (fr-FR) UI text map — Textes d'interface en français.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val FrFrUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Utiliser le micro",
    UiTextKey.CopyButton to "Copier",
    UiTextKey.SpeakScriptButton to "Lire le texte original",
    UiTextKey.TranslateButton to "Traduire",
    UiTextKey.CopyTranslationButton to "Copier la traduction",
    UiTextKey.SpeakTranslationButton to "Lire la traduction",
    UiTextKey.RecognizingStatus to "Enregistrement...Parlez maintenant, arrêt automatique.",
    UiTextKey.TranslatingStatus to "Traduction en cours...",
    UiTextKey.SpeakingOriginalStatus to "Lecture de l'original...",
    UiTextKey.SpeakingTranslationStatus to "Lecture de la traduction...",
    UiTextKey.SpeakingLabel to "Lecture",
    UiTextKey.FinishedSpeakingOriginal to "Lecture de l'original terminée",
    UiTextKey.FinishedSpeakingTranslation to "Lecture de la traduction terminée",
    UiTextKey.TtsErrorTemplate to "Erreur vocale : %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Langue d'affichage",
    UiTextKey.DetectLanguageLabel to "Détecter la langue",
    UiTextKey.TranslateToLabel to "Traduire vers",

    // Language names
    UiTextKey.LangEnUs to "Anglais",
    UiTextKey.LangZhHk to "Cantonais",
    UiTextKey.LangJaJp to "Japonais",
    UiTextKey.LangZhCn to "Chinois (simplifié)",
    UiTextKey.LangFrFr to "Français",
    UiTextKey.LangDeDe to "Allemand",
    UiTextKey.LangKoKr to "Coréen",
    UiTextKey.LangEsEs to "Espagnol",
    UiTextKey.LangIdId to "Indonésien",
    UiTextKey.LangViVn to "Vietnamien",
    UiTextKey.LangThTh to "Thaï",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Malais",
    UiTextKey.LangPtBr to "Portugais",
    UiTextKey.LangItIt to "Italien",
    UiTextKey.LangRuRu to "Russe",

    // Navigation
    UiTextKey.NavHistory to "Historique",
    UiTextKey.NavLogin to "Connexion",
    UiTextKey.NavLogout to "Déconnexion",
    UiTextKey.NavBack to "Retour",
    UiTextKey.ActionCancel to "Annuler",
    UiTextKey.ActionDelete to "Supprimer",
    UiTextKey.ActionOpen to "Ouvrir",
    UiTextKey.ActionName to "Nommer",
    UiTextKey.ActionSave to "Enregistrer",
    UiTextKey.ActionConfirm to "Confirmer",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Saisissez ici ou utilisez le micro...",
    UiTextKey.SpeechTranslatedPlaceholder to "La traduction apparaîtra ici...",
    UiTextKey.StatusAzureErrorTemplate to "Erreur Azure : %s",
    UiTextKey.StatusTranslationErrorTemplate to "Erreur de traduction : %s",
    UiTextKey.StatusLoginRequiredTranslation to "Connexion requise pour traduire",
    UiTextKey.StatusRecognizePreparing to "Préparation du micro...(ne parlez pas)",
    UiTextKey.StatusRecognizeListening to "Écoute...Parlez maintenant.",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Page précédente",
    UiTextKey.PaginationNextLabel to "Page suivante",
    UiTextKey.PaginationPageLabelTemplate to "Page {page} sur {total}",

    // Toast
    UiTextKey.ToastCopied to "Copié",
    UiTextKey.DisableText to "Connectez-vous pour utiliser la traduction et enregistrer l'historique.",

    // Error
    UiTextKey.ErrorRetryButton to "Réessayer",
    UiTextKey.ErrorGenericMessage to "Une erreur est survenue. Veuillez réessayer.",

    // Shop
    UiTextKey.ShopTitle to "Boutique",
    UiTextKey.ShopCoinBalance to "Mes pièces",
    UiTextKey.ShopHistoryExpansionTitle to "Extension d'historique",
    UiTextKey.ShopHistoryExpansionDesc to "Augmentez votre limite d'historique pour voir plus de traductions.",
    UiTextKey.ShopCurrentLimit to "Limite actuelle : {limit} entrées",
    UiTextKey.ShopMaxLimit to "Limite max :",
    UiTextKey.ShopBuyHistoryExpansion to "Acheter (+{increment} entrées, {cost} pièces)",
    UiTextKey.ShopInsufficientCoins to "Pièces insuffisantes",
    UiTextKey.ShopMaxLimitReached to "Limite maximum atteinte",
    UiTextKey.ShopHistoryExpandedTitle to "Extension réussie !",
    UiTextKey.ShopHistoryExpandedMessage to "Votre limite d'historique est maintenant de {limit} entrées ! Vous pouvez voir plus de traductions !",
    UiTextKey.ShopColorPaletteTitle to "Thèmes de couleur",
    UiTextKey.ShopColorPaletteDesc to "Choisissez le thème de couleur de votre app, 10 pièces chacun",
    UiTextKey.ShopEntry to "Boutique",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Paramètres vocaux",
    UiTextKey.VoiceSettingsDesc to "Sélectionnez la voix de lecture pour chaque langue.",

    // Instructions
    UiTextKey.SpeechInstructions to "Appuyez sur le micro pour démarrer la reconnaissance vocale, puis appuyez sur traduire. Si la détection automatique ne se met pas à jour après un changement de texte ou de langue, appuyez sur actualiser en haut à droite.",
    UiTextKey.HomeInstructions to "Sélectionnez une fonctionnalité pour commencer.",
    UiTextKey.ContinuousInstructions to "Sélectionnez deux langues et démarrez le mode conversation.",

    // Home
    UiTextKey.HomeTitle to "Traduction instantanée",
    UiTextKey.HelpTitle to "Aide",
    UiTextKey.SpeechTitle to "Traduction rapide",
    UiTextKey.HomeStartButton to "Démarrer la traduction",
    UiTextKey.HomeFeaturesTitle to "Fonctionnalités",
    UiTextKey.HomeDiscreteDescription to "Traduction courte de texte et de voix",
    UiTextKey.HomeContinuousDescription to "Traduction bidirectionnelle en temps réel",
    UiTextKey.HomeLearningDescription to "Générez du matériel d'apprentissage et des quiz à partir de l'historique",

    // Help
    UiTextKey.HelpCurrentTitle to "Fonctionnalités actuelles",
    UiTextKey.HelpCautionTitle to "Précautions",
    UiTextKey.HelpCurrentFeatures to "Fonctionnalités actuelles :\n" +
            "  • Traduction rapide : reconnaissance vocale puis traduction\n" +
            "  • Conversation en direct : traduction vocale bidirectionnelle\n" +
            "  • Historique : consulter l'historique de traduction\n" +
            "  • Matériel d'apprentissage : générer vocabulaire et quiz\n\n" +
            "Traduction :\n" +
            "  • Reconnaissance vocale Azure AI\n" +
            "  • Service de traduction Azure\n",
    UiTextKey.HelpCaution to "Précautions :\n" +
            "  • La reconnaissance vocale nécessite une connexion internet\n" +
            "  • Le cache de traduction local est disponible hors ligne\n" +
            "  • Vérifiez les traductions importantes avec un service professionnel\n\n" +
            "Compte et données :\n" +
            "  • L'historique, l'apprentissage et les pièces nécessitent une connexion\n" +
            "  • Les données sont stockées en sécurité sur Firebase Firestore\n\n" +
            "Dépannage :\n" +
            "  • Si rien ne fonctionne, redémarrez l'application\n",
    UiTextKey.HelpNotesTitle to "Notes",
    UiTextKey.HelpNotes to "💡 Conseils d'utilisation :\n\n" +
            "Pour de meilleures traductions :\n" +
            "  • Parlez clairement et à un rythme modéré\n" +
            "  • Réduisez le bruit ambiant pour une meilleure précision\n" +
            "  • La traduction rapide convient aux phrases courtes\n\n" +
            "Langue d'affichage :\n" +
            "  • Langue par défaut : anglais, autres langues via IA\n" +
            "  • La version cantonaise est traduite manuellement\n" +
            "Mises à jour et commentaires :\n" +
            "  • Version de l'app dans Paramètres → À propos\n" +
            "  • Envoyez vos commentaires dans Paramètres → Commentaires\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Commentaires",
    UiTextKey.FeedbackDesc to "Merci pour vos commentaires ! Faites-nous part de vos suggestions, signalements de bugs ou avis.",
    UiTextKey.FeedbackMessagePlaceholder to "Saisissez votre commentaire...",
    UiTextKey.FeedbackSubmitButton to "Envoyer",
    UiTextKey.FeedbackSubmitting to "Envoi en cours...",
    UiTextKey.FeedbackSuccessTitle to "Merci !",
    UiTextKey.FeedbackSuccessMessage to "Votre commentaire a été envoyé avec succès. Merci !",
    UiTextKey.FeedbackErrorTitle to "Échec de l'envoi",
    UiTextKey.FeedbackErrorMessage to "L'envoi a échoué. Vérifiez votre connexion et réessayez.",
    UiTextKey.FeedbackMessageRequired to "Veuillez saisir votre commentaire.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Conversation en direct",
    UiTextKey.ContinuousStartButton to "Démarrer la conversation",
    UiTextKey.ContinuousStopButton to "Arrêter l'enregistrement",
    UiTextKey.ContinuousStartScreenButton to "Conversation en direct",
    UiTextKey.ContinuousPersonALabel to "A parle",
    UiTextKey.ContinuousPersonBLabel to "B parle",
    UiTextKey.ContinuousCurrentStringLabel to "Texte actuel :",
    UiTextKey.ContinuousSpeakerAName to "Personne A",
    UiTextKey.ContinuousSpeakerBName to "Personne B",
    UiTextKey.ContinuousTranslationSuffix to " · Traduction",
    UiTextKey.ContinuousPreparingMicText to "Préparation du micro...(ne parlez pas)",
    UiTextKey.ContinuousTranslatingText to "Traduction en cours...",

    // History
    UiTextKey.HistoryTitle to "Historique",
    UiTextKey.HistoryTabDiscrete to "Traduction rapide",
    UiTextKey.HistoryTabContinuous to "Conversation en direct",
    UiTextKey.HistoryNoContinuousSessions to "Aucune session de conversation.",
    UiTextKey.HistoryNoDiscreteRecords to "Aucun enregistrement de traduction.",
    UiTextKey.DialogDeleteRecordTitle to "Supprimer l'enregistrement ?",
    UiTextKey.DialogDeleteRecordMessage to "Cette action est irréversible.",
    UiTextKey.DialogDeleteSessionTitle to "Supprimer la conversation ?",
    UiTextKey.DialogDeleteSessionMessage to "Tous les enregistrements de cette conversation seront supprimés. Irréversible.",
    UiTextKey.HistoryDeleteSessionButton to "Supprimer",
    UiTextKey.HistoryNameSessionTitle to "Nommer",
    UiTextKey.HistorySessionNameLabel to "Nom de la conversation",
    UiTextKey.HistorySessionTitleTemplate to "Conversation {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} entrées",

    // Filter
    UiTextKey.FilterDropdownDefault to "Toutes les langues",
    UiTextKey.FilterTitle to "Filtrer l'historique",
    UiTextKey.FilterLangDrop to "Langue",
    UiTextKey.FilterKeyword to "Mot-clé",
    UiTextKey.FilterApply to "Appliquer",
    UiTextKey.FilterCancel to "Annuler",
    UiTextKey.FilterClear to "Effacer",
    UiTextKey.FilterHistoryScreenTitle to "Filtre",

    // Auth
    UiTextKey.AuthLoginTitle to "Connexion",
    UiTextKey.AuthRegisterTitle to "Inscription (suspendue)",
    UiTextKey.AuthLoginHint to "Utilisez votre e-mail et mot de passe enregistrés.",
    UiTextKey.AuthRegisterRules to "L'inscription est suspendue pendant le développement.\nAttention : un e-mail inexistant empêchera la réinitialisation du mot de passe.\n" +
            "Règles d'inscription :\n" +
            "• Format d'e-mail valide (ex : nom@exemple.com)\n" +
            "• Mot de passe d'au moins 8 caractères\n" +
            "• Confirmation du mot de passe identique",
    UiTextKey.AuthEmailLabel to "E-mail",
    UiTextKey.AuthPasswordLabel to "Mot de passe",
    UiTextKey.AuthConfirmPasswordLabel to "Confirmer le mot de passe",
    UiTextKey.AuthLoginButton to "Se connecter",
    UiTextKey.AuthRegisterButton to "S'inscrire",
    UiTextKey.AuthToggleToRegister to "Pas de compte ? S'inscrire (suspendu)",
    UiTextKey.AuthToggleToLogin to "Déjà un compte ? Se connecter",
    UiTextKey.AuthErrorPasswordsMismatch to "Les mots de passe ne correspondent pas.",
    UiTextKey.AuthErrorPasswordTooShort to "Le mot de passe doit contenir au moins 8 caractères.",
    UiTextKey.AuthRegistrationDisabled to "L'inscription est suspendue pendant le développement.",
    UiTextKey.AuthResetEmailSent to "E-mail de réinitialisation envoyé (si l'e-mail existe). Vérifiez votre boîte de réception.",

    // Password reset
    UiTextKey.ForgotPwText to "Mot de passe oublié ?",
    UiTextKey.ResetPwTitle to "Réinitialiser le mot de passe",
    UiTextKey.ResetPwText to "Entrez l'e-mail de votre compte. Nous enverrons un lien de réinitialisation.\nAssurez-vous que c'est l'e-mail enregistré.\n",
    UiTextKey.ResetSendingText to "Envoi en cours...",
    UiTextKey.ResetSendText to "Envoyer l'e-mail de réinitialisation",

    // Settings
    UiTextKey.SettingsTitle to "Paramètres",
    UiTextKey.SettingsPrimaryLanguageTitle to "Langue principale",
    UiTextKey.SettingsPrimaryLanguageDesc to "Utilisée pour les explications et suggestions d'apprentissage",
    UiTextKey.SettingsPrimaryLanguageLabel to "Langue principale",
    UiTextKey.SettingsFontSizeTitle to "Taille de police",
    UiTextKey.SettingsFontSizeDesc to "Ajustez la taille du texte pour une meilleure lisibilité (synchronisée entre les appareils)",
    UiTextKey.SettingsScaleTemplate to "Taille : {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Thème de couleur",
    UiTextKey.SettingsColorPaletteDesc to "Choisissez le thème de couleur, 10 pièces chacun",
    UiTextKey.SettingsColorCostTemplate to "{cost} pièces",
    UiTextKey.SettingsColorUnlockButton to "Déverrouiller",
    UiTextKey.SettingsColorSelectButton to "Sélectionner",
    UiTextKey.SettingsColorAlreadyUnlocked to "Déverrouillé",
    UiTextKey.SettingsPreviewHeadline to "Titre : Aperçu grand texte",
    UiTextKey.SettingsPreviewBody to "Corps : Aperçu texte normal",
    UiTextKey.SettingsPreviewLabel to "Libellé : Aperçu petit texte",
    UiTextKey.SettingsAboutTitle to "À propos",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Connecté, les paramètres sont automatiquement sauvegardés et synchronisés.",
    UiTextKey.SettingsThemeTitle to "Thème",
    UiTextKey.SettingsThemeDesc to "Choisissez l'apparence : Système, Clair, Sombre ou Programmé.",
    UiTextKey.SettingsThemeSystem to "Système",
    UiTextKey.SettingsThemeLight to "Clair",
    UiTextKey.SettingsThemeDark to "Sombre",
    UiTextKey.SettingsThemeScheduled to "Programmé",
    UiTextKey.SettingsResetPW to "Réinitialiser le mot de passe",
    UiTextKey.SettingsQuickLinks to "Paramètres détaillés",
    UiTextKey.SettingsNotLoggedInWarning to "Connectez-vous pour accéder aux paramètres du compte. Vous pouvez toujours changer la langue de l'app.",
    UiTextKey.SettingsVoiceTitle to "Paramètres vocaux",
    UiTextKey.SettingsVoiceDesc to "Sélectionnez la voix de lecture pour chaque langue.",
    UiTextKey.SettingsVoiceLanguageLabel to "Langue",
    UiTextKey.SettingsVoiceSelectLabel to "Voix",
    UiTextKey.SettingsVoiceDefault to "Par défaut",

    // Learning
    UiTextKey.LearningTitle to "Apprentissage",
    UiTextKey.LearningHintCount to "(*) Nombre = traductions impliquant cette langue.",
    UiTextKey.LearningErrorTemplate to "Erreur : %s",
    UiTextKey.LearningGenerate to "Générer",
    UiTextKey.LearningRegenerate to "Régénérer",
    UiTextKey.LearningGenerating to "Génération en cours...",
    UiTextKey.LearningOpenSheetTemplate to "Fiche de {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Fiche d'apprentissage {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Langue principale : {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Enregistrements actuels : {nowCount} (à la génération : {savedCount})",
    UiTextKey.LearningSheetNoContent to "Aucun contenu de fiche.",
    UiTextKey.LearningSheetRegenerate to "Régénérer",
    UiTextKey.LearningSheetGenerating to "Génération en cours...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Qu'est-ce que c'est ?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Fiche d'apprentissage générée à partir de votre historique de traduction. Elle contient vocabulaire, définitions, exemples et points de grammaire. Testez vos connaissances avec le quiz ci-dessous !",
    UiTextKey.LearningRegenBlockedTitle to "Impossible de régénérer pour le moment",
    UiTextKey.LearningRegenBlockedMessage to "Pour régénérer, il faut au moins 5 enregistrements de plus qu'avant. Il en manque {needed}.",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Il manque {needed} enregistrements pour régénérer (min. 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Le nombre d'enregistrements doit dépasser celui de la dernière génération",
    UiTextKey.LearningRegenInfoTitle to "Règles de régénération",
    UiTextKey.LearningRegenInfoMessage to "Pour régénérer le matériel d'apprentissage :\n\n• Première génération : à tout moment\n• Régénération : au moins 5 enregistrements de plus\n\nLe bouton devient bleu quand il y a assez d'enregistrements. S'il est gris, continuez à traduire !\n\n💡 Astuce : si le nombre ne se met pas à jour, redémarrez l'app.",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Un quiz a déjà été généré pour cette version. Générez une nouvelle fiche pour créer un nouveau quiz.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz : {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Générer un quiz",
    UiTextKey.QuizGenerating to "⏳ Génération...",
    UiTextKey.QuizUpToDate to "✓ À jour",
    UiTextKey.QuizBlocked to "🚫 Bloqué",
    UiTextKey.QuizWait to "⏳ Patientez...",
    UiTextKey.QuizMaterialsQuizTemplate to "Matériel : {materials} | Quiz : {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Vous pouvez gagner des pièces !",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 {count} de plus pour gagner des pièces",
    UiTextKey.QuizCancelButton to "Annuler",
    UiTextKey.QuizPreviousButton to "Question précédente",
    UiTextKey.QuizNextButton to "Question suivante",
    UiTextKey.QuizSubmitButton to "Soumettre",
    UiTextKey.QuizRetakeButton to "Refaire",
    UiTextKey.QuizBackButton to "Retour",
    UiTextKey.QuizLoadingText to "Chargement du quiz...",
    UiTextKey.QuizGeneratingText to "Génération du quiz...",
    UiTextKey.QuizNoMaterialsTitle to "Matériel introuvable",
    UiTextKey.QuizNoMaterialsMessage to "Générez d'abord le matériel d'apprentissage, puis accédez au quiz.",
    UiTextKey.QuizErrorTitle to "⚠️ Erreur de quiz",
    UiTextKey.QuizErrorSuggestion to "Suggestion : utilisez le bouton ci-dessus pour générer un quiz.",
    UiTextKey.QuizCompletedTitle to "Quiz terminé !",
    UiTextKey.QuizAnswerReviewTitle to "Révision des réponses",
    UiTextKey.QuizYourAnswerTemplate to "Votre réponse : {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Bonne réponse : {Answer}",
    UiTextKey.QuizQuestionTemplate to "Question {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Impossible de régénérer : matériel({materials}) < quiz({quiz}), ajoutez plus de traductions.",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Une autre génération est en cours. Veuillez patienter.",
    UiTextKey.QuizCoinRulesTitle to "🪙 Règles des pièces",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Comment gagner :",
    UiTextKey.QuizCoinRulesRequirements to "Conditions :",
    UiTextKey.QuizCoinRulesCurrentStatus to "Statut actuel :",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Vous pouvez gagner des pièces au prochain quiz !",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• {count} de plus pour les pièces",
    UiTextKey.QuizCoinRule1Coin to "• 1 pièce par bonne réponse",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Seule la première tentative compte",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Le quiz doit correspondre à la version du matériel",
    UiTextKey.QuizCoinRulePlus10 to "• Au moins 10 enregistrements de plus que le dernier quiz rémunéré",
    UiTextKey.QuizCoinRuleNoDelete to "• Impossible de regagner des pièces après suppression d'enregistrements",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Matériel : {count} enregistrements",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Quiz : {count} enregistrements",
    UiTextKey.QuizCoinRuleGotIt to "Compris !",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Générer un nouveau quiz ?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Vous pouvez gagner des pièces avec ce quiz ! (première tentative uniquement)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Vous ne pouvez pas gagner de pièces avec ce quiz.",
    UiTextKey.QuizRegenNeedMoreTemplate to "Il vous faut {count} traductions de plus pour l'éligibilité aux pièces (10 de plus que le dernier quiz rémunéré).",
    UiTextKey.QuizRegenReminder to "Astuce : vous pouvez vous entraîner et réessayer, mais les pièces ne sont attribuées qu'à la première tentative avec suffisamment d'enregistrements.",
    UiTextKey.QuizRegenGenerateButton to "Générer",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Pièces gagnées !",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Félicitations ! Vous avez gagné {Coins} pièces !",
    UiTextKey.QuizCoinsRule1 to "• 1 pièce par bonne réponse à la première tentative",
    UiTextKey.QuizCoinsRule2 to "• Pas de pièces pour les tentatives suivantes",
    UiTextKey.QuizCoinsRule3 to "• Les nouveaux quiz nécessitent 10 enregistrements de plus",
    UiTextKey.QuizCoinsRule4 to "• Le quiz doit correspondre à la version du matériel",
    UiTextKey.QuizCoinsRule5 to "• Total des pièces visible dans l'historique",
    UiTextKey.QuizCoinsGreatButton to "Super !",
    UiTextKey.QuizOutdatedMessage to "Ce quiz est basé sur une ancienne fiche d'apprentissage.",
    UiTextKey.QuizRecordsLabel to "enregistrements",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Mes pièces",
    UiTextKey.HistoryCoinRulesTitle to "Règles des pièces :",
    UiTextKey.HistoryCoinHowToEarnTitle to "Comment gagner :",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 pièce par bonne réponse",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Seule la première tentative par version",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Le quiz doit correspondre au matériel actuel",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Règles anti-triche :",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Au moins 10 nouvelles traductions depuis le dernier gain",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• La version du quiz doit correspondre au matériel",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• La suppression d'enregistrements bloque la régénération du quiz (sauf si le nombre dépasse le précédent)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Pas de pièces pour les tentatives suivantes",
    UiTextKey.HistoryCoinTipsTitle to "💡 Conseils :",
    UiTextKey.HistoryCoinTipsRule1 to "• Ajoutez régulièrement des traductions",
    UiTextKey.HistoryCoinTipsRule2 to "• Étudiez bien avant votre première tentative !",
    UiTextKey.HistoryCoinGotItButton to "Compris !",

    // History info
    UiTextKey.HistoryInfoTitle to "Infos historique",
    UiTextKey.HistoryInfoLimitMessage to "L'historique affiche les {limit} entrées les plus récentes. Étendez la limite dans la boutique !",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Les enregistrements plus anciens sont conservés mais masqués pour la performance.",
    UiTextKey.HistoryInfoFavoritesMessage to "Pour sauvegarder définitivement une traduction, appuyez sur ❤️ dans l'historique pour l'ajouter aux favoris.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Consultez vos sauvegardes dans Paramètres → Favoris.",
    UiTextKey.HistoryInfoFilterMessage to "Utilisez le filtre pour chercher dans les {limit} entrées affichées.",
    UiTextKey.HistoryInfoGotItButton to "Compris",

    // Word bank
    UiTextKey.WordBankTitle to "Banque de mots",
    UiTextKey.WordBankSelectLanguage to "Sélectionnez une langue pour voir ou générer votre banque de mots :",
    UiTextKey.WordBankNoHistory to "Aucun historique de traduction",
    UiTextKey.WordBankNoHistoryHint to "Commencez à traduire pour créer votre banque de mots !",
    UiTextKey.WordBankWordsCount to "mots",
    UiTextKey.WordBankGenerating to "Génération en cours...",
    UiTextKey.WordBankGenerate to "Générer la banque de mots",
    UiTextKey.WordBankRegenerate to "Régénérer la banque de mots",
    UiTextKey.WordBankRefresh to "🔄 Mettre à jour la banque",
    UiTextKey.WordBankEmpty to "Banque de mots vide",
    UiTextKey.WordBankEmptyHint to "Appuyez sur le bouton ci-dessus pour générer à partir de votre historique.",
    UiTextKey.WordBankExample to "Exemple :",
    UiTextKey.WordBankDifficulty to "Difficulté :",
    UiTextKey.WordBankFilterCategory to "Catégorie",
    UiTextKey.WordBankFilterCategoryAll to "Toutes les catégories",
    UiTextKey.WordBankFilterDifficultyLabel to "Difficulté :",
    UiTextKey.WordBankFilterNoResults to "Aucun mot ne correspond aux filtres",
    UiTextKey.WordBankRefreshAvailable to "✅ Mise à jour disponible !",
    UiTextKey.WordBankRecordsNeeded to "entrées (20 nécessaires pour la mise à jour)",
    UiTextKey.WordBankRegenInfoTitle to "Règles de mise à jour",
    UiTextKey.WordBankRegenInfoMessage to "Pour mettre à jour la banque de mots :\n\n• Première génération : à tout moment\n• Mise à jour : au moins 20 enregistrements de plus\n\nLe bouton devient bleu quand il y en a assez. S'il est gris, continuez à traduire !\n\n💡 Astuce : si le nombre ne se met pas à jour, redémarrez l'app.",
    UiTextKey.WordBankHistoryCountTemplate to "Enregistrements actuels : {nowCount} (à la génération : {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Se déconnecter ?",
    UiTextKey.DialogLogoutMessage to "La traduction et l'accès à l'historique nécessiteront une reconnexion.",
    UiTextKey.DialogGenerateOverwriteTitle to "Écraser le matériel ?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Le matériel précédent sera écrasé (s'il existe).\nGénérer le matériel pour {speclanguage} ?",

    // Profile
    UiTextKey.ProfileTitle to "Profil",
    UiTextKey.ProfileUsernameLabel to "Nom d'utilisateur",
    UiTextKey.ProfileUsernameHint to "Saisissez un nom d'utilisateur",
    UiTextKey.ProfileUpdateButton to "Mettre à jour le profil",
    UiTextKey.ProfileUpdateSuccess to "Profil mis à jour",
    UiTextKey.ProfileUpdateError to "Échec de la mise à jour",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Supprimer le compte",
    UiTextKey.AccountDeleteWarning to "⚠️ Cette action est permanente et irréversible !",
    UiTextKey.AccountDeleteConfirmMessage to "Toutes les données seront supprimées définitivement : historique, banque de mots, matériel d'apprentissage, paramètres. Entrez votre mot de passe pour confirmer.",
    UiTextKey.AccountDeletePasswordLabel to "Mot de passe",
    UiTextKey.AccountDeleteButton to "Supprimer le compte",
    UiTextKey.AccountDeleteSuccess to "Compte supprimé avec succès",
    UiTextKey.AccountDeleteError to "Échec de la suppression",
    UiTextKey.AccountDeleteReauthRequired to "Entrez votre mot de passe pour confirmer la suppression",

    // Favorites
    UiTextKey.FavoritesTitle to "Favoris",
    UiTextKey.FavoritesEmpty to "Aucun favori",
    UiTextKey.FavoritesAddSuccess to "Ajouté aux favoris",
    UiTextKey.FavoritesRemoveSuccess to "Retiré des favoris",
    UiTextKey.FavoritesAddButton to "Ajouter aux favoris",
    UiTextKey.FavoritesRemoveButton to "Retirer des favoris",
    UiTextKey.FavoritesNoteLabel to "Note",
    UiTextKey.FavoritesNoteHint to "Ajouter une note (optionnel)",
    UiTextKey.FavoritesTabRecords to "Enregistrements",
    UiTextKey.FavoritesTabSessions to "Conversations",
    UiTextKey.FavoritesSessionsEmpty to "Aucune conversation sauvegardée",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} messages",

    // Custom words
    UiTextKey.CustomWordsTitle to "Mots personnalisés",
    UiTextKey.CustomWordsAdd to "Ajouter un mot",
    UiTextKey.CustomWordsEdit to "Modifier le mot",
    UiTextKey.CustomWordsDelete to "Supprimer le mot",
    UiTextKey.CustomWordsOriginalLabel to "Mot original",
    UiTextKey.CustomWordsTranslatedLabel to "Traduction",
    UiTextKey.CustomWordsPronunciationLabel to "Prononciation (optionnel)",
    UiTextKey.CustomWordsExampleLabel to "Exemple (optionnel)",
    UiTextKey.CustomWordsSaveSuccess to "Mot enregistré",
    UiTextKey.CustomWordsDeleteSuccess to "Mot supprimé",
    UiTextKey.CustomWordsAlreadyExists to "Ce mot existe déjà",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Langue d'origine",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Langue de traduction",
    UiTextKey.CustomWordsSaveButton to "Enregistrer",
    UiTextKey.CustomWordsCancelButton to "Annuler",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Détection auto",
    UiTextKey.LanguageDetectDetecting to "Détection...",
    UiTextKey.LanguageDetectedTemplate to "Détecté : {language}",
    UiTextKey.LanguageDetectFailed to "Échec de la détection",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Scanner du texte depuis une image",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Attention : la reconnaissance de texte par image peut ne pas être totalement exacte. Vérifiez le texte extrait." +
            "Supporte le latin (anglais, etc.), le chinois, le japonais et le coréen.",
    UiTextKey.ImageRecognitionScanning to "Scan du texte en cours...",
    UiTextKey.ImageRecognitionSuccess to "Texte extrait avec succès",

    // Cache
    UiTextKey.CacheClearButton to "Vider le cache",
    UiTextKey.CacheClearSuccess to "Cache vidé",
    UiTextKey.CacheStatsTemplate to "Cache : {count} traductions enregistrées",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Thème automatique",
    UiTextKey.SettingsAutoThemeDesc to "Basculer automatiquement entre clair et sombre selon l'heure",
    UiTextKey.SettingsAutoThemeEnabled to "Activé",
    UiTextKey.SettingsAutoThemeDisabled to "Désactivé",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Début du mode sombre :",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Début du mode clair :",
    UiTextKey.SettingsAutoThemePreview to "Le thème changera automatiquement aux heures définies",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Mode hors ligne",
    UiTextKey.OfflineModeMessage to "Vous êtes actuellement hors ligne. Affichage des données en cache.",
    UiTextKey.OfflineModeRetry to "Réessayer la connexion",
    UiTextKey.OfflineDataCached to "Données en cache disponibles",
    UiTextKey.OfflineSyncPending to "Les modifications seront synchronisées en ligne",

    // Image capture
    UiTextKey.ImageSourceTitle to "Choisir la source de l'image",
    UiTextKey.ImageSourceCamera to "Prendre une photo",
    UiTextKey.ImageSourceGallery to "Choisir dans la galerie",
    UiTextKey.ImageSourceCancel to "Annuler",
    UiTextKey.CameraCaptureContentDesc to "Capturer",

    // Friends
    UiTextKey.FriendsTitle to "Amis",
    UiTextKey.FriendsMenuButton to "Amis",
    UiTextKey.FriendsAddButton to "Ajouter un ami",
    UiTextKey.FriendsSearchTitle to "Rechercher un utilisateur",
    UiTextKey.FriendsSearchPlaceholder to "Nom d'utilisateur ou ID...",
    UiTextKey.FriendsSearchMinChars to "Entrez au moins 2 caractères",
    UiTextKey.FriendsSearchNoResults to "Aucun utilisateur trouvé",
    UiTextKey.FriendsListEmpty to "Ajoutez des amis pour échanger et partager du matériel d'apprentissage.",
    UiTextKey.FriendsRequestsSection to "Demandes d'amis ({count})",
    UiTextKey.FriendsSectionTitle to "Amis ({count})",
    UiTextKey.FriendsAcceptButton to "Accepter",
    UiTextKey.FriendsRejectButton to "Refuser",
    UiTextKey.FriendsRemoveButton to "Supprimer",
    UiTextKey.FriendsRemoveDialogTitle to "Supprimer l'ami",
    UiTextKey.FriendsRemoveDialogMessage to "Supprimer {username} de votre liste d'amis ?",
    UiTextKey.FriendsSendRequestButton to "Ajouter",
    UiTextKey.FriendsRequestSentSuccess to "Demande d'ami envoyée !",
    UiTextKey.FriendsRequestAcceptedSuccess to "Demande acceptée !",
    UiTextKey.FriendsRequestRejectedSuccess to "Demande refusée",
    UiTextKey.FriendsRemovedSuccess to "Ami supprimé",
    UiTextKey.FriendsRequestFailed to "Échec de l'envoi",
    UiTextKey.FriendsCloseButton to "Fermer",
    UiTextKey.FriendsCancelButton to "Annuler",
    UiTextKey.FriendsRemoveConfirm to "Supprimer",
    UiTextKey.FriendsNewRequestsTemplate to "Vous avez {count} nouvelle(s) demande(s) d'ami !",
    UiTextKey.FriendsSentRequestsSection to "Demandes envoyées ({count})",
    UiTextKey.FriendsPendingStatus to "En attente",
    UiTextKey.FriendsCancelRequestButton to "Annuler la demande",
    UiTextKey.FriendsUnreadMessageDesc to "Envoyer un message",
    UiTextKey.FriendsDeleteModeButton to "Supprimer des amis",
    UiTextKey.FriendsDeleteSelectedButton to "Supprimer la sélection",
    UiTextKey.FriendsDeleteMultipleTitle to "Supprimer des amis",
    UiTextKey.FriendsDeleteMultipleMessage to "Supprimer les {count} amis sélectionnés ?",
    UiTextKey.FriendsSearchMinChars3 to "Entrez au moins 3 caractères pour le nom",
    UiTextKey.FriendsSearchByUserIdHint to "Ou cherchez exactement par ID utilisateur",
    UiTextKey.FriendsStatusAlreadyFriends to "Déjà amis",
    UiTextKey.FriendsStatusRequestSent to "Demande envoyée — en attente de réponse",
    UiTextKey.FriendsStatusRequestReceived to "Cet utilisateur vous a envoyé une demande",

    // Chat
    UiTextKey.ChatTitle to "Chat avec {username}",
    UiTextKey.ChatInputPlaceholder to "Tapez un message...",
    UiTextKey.ChatSendButton to "Envoyer",
    UiTextKey.ChatEmpty to "Aucun message. Commencez à discuter !",
    UiTextKey.ChatMessageSent to "Message envoyé",
    UiTextKey.ChatMessageFailed to "Échec de l'envoi",
    UiTextKey.ChatMarkingRead to "Marquage en cours...",
    UiTextKey.ChatLoadingMessages to "Chargement des messages...",
    UiTextKey.ChatToday to "Aujourd'hui",
    UiTextKey.ChatYesterday to "Hier",
    UiTextKey.ChatUnreadBadge to "{count} non lu(s)",
    UiTextKey.ChatTranslateButton to "Traduire",
    UiTextKey.ChatTranslateDialogTitle to "Traduire la conversation",
    UiTextKey.ChatTranslateDialogMessage to "Traduire les messages de votre ami dans votre langue ? La langue de chaque message sera détectée et traduite.",
    UiTextKey.ChatTranslateConfirm to "Tout traduire",
    UiTextKey.ChatTranslating to "Traduction des messages...",
    UiTextKey.ChatTranslated to "Messages traduits",
    UiTextKey.ChatShowOriginal to "Afficher l'original",
    UiTextKey.ChatShowTranslation to "Afficher la traduction",
    UiTextKey.ChatTranslateFailed to "Échec de la traduction",
    UiTextKey.ChatTranslatedLabel to "Traduit",

    // Sharing
    UiTextKey.ShareTitle to "Partager",
    UiTextKey.ShareInboxTitle to "Boîte de partage",
    UiTextKey.ShareInboxEmpty to "Aucun partage. Vos amis peuvent partager des mots et du matériel !",
    UiTextKey.ShareWordButton to "Partager le mot",
    UiTextKey.ShareMaterialButton to "Partager le matériel",
    UiTextKey.ShareSelectFriendTitle to "Choisir un ami",
    UiTextKey.ShareSelectFriendMessage to "Sélectionnez un ami pour partager :",
    UiTextKey.ShareSuccess to "Partagé avec succès !",
    UiTextKey.ShareFailed to "Échec du partage",
    UiTextKey.ShareWordWith to "Partager un mot avec {username}",
    UiTextKey.ShareMaterialWith to "Partager du matériel avec {username}",
    UiTextKey.ShareAcceptButton to "Accepter",
    UiTextKey.ShareDismissButton to "Ignorer",
    UiTextKey.ShareAccepted to "Ajouté à votre collection",
    UiTextKey.ShareDismissed to "Élément ignoré",
    UiTextKey.ShareActionFailed to "Action échouée",
    UiTextKey.ShareTypeWord to "Mot",
    UiTextKey.ShareTypeLearningSheet to "Fiche d'apprentissage",
    UiTextKey.ShareReceivedFrom to "De : {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} nouvel(aux) élément(s) !",
    UiTextKey.ShareViewFullMaterial to "Appuyez sur « Voir » pour consulter le matériel complet",
    UiTextKey.ShareDeleteItemTitle to "Supprimer l'élément",
    UiTextKey.ShareDeleteItemMessage to "Supprimer cet élément partagé ? Irréversible.",
    UiTextKey.ShareDeleteButton to "Supprimer",
    UiTextKey.ShareViewButton to "Voir",
    UiTextKey.ShareItemNotFound to "Élément introuvable.",
    UiTextKey.ShareNoContent to "Aucun contenu dans ce matériel.",
    UiTextKey.ShareSaveToSelf to "Enregistrer dans ma boîte",
    UiTextKey.ShareSavedToSelf to "Enregistré dans votre boîte !",

    // My profile
    UiTextKey.MyProfileTitle to "Mon profil",
    UiTextKey.MyProfileUserId to "ID utilisateur",
    UiTextKey.MyProfileUsername to "Nom d'utilisateur",
    UiTextKey.MyProfileDisplayName to "Nom affiché",
    UiTextKey.MyProfileCopyUserId to "Copier l'ID",
    UiTextKey.MyProfileCopyUsername to "Copier le nom",
    UiTextKey.MyProfileShare to "Partager le profil",
    UiTextKey.MyProfileCopied to "Copié dans le presse-papiers !",
    UiTextKey.MyProfileLanguages to "Langues",
    UiTextKey.MyProfilePrimaryLanguage to "Langue principale",
    UiTextKey.MyProfileLearningLanguages to "Langues en apprentissage",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Page Amis",
    UiTextKey.FriendsInfoMessage to "• Tirez vers le bas pour actualiser la liste, les demandes et le statut.\n" +
            "• Appuyez sur une carte pour ouvrir le chat.\n" +
            "• Le point rouge (●) indique des messages non lus, ✓✓ pour tout marquer comme lu.\n" +
            "• L'icône 📥 pour la boîte de partage, ✓✓ pour effacer le point.\n" +
            "• L'icône 🚫 pour bloquer — l'ami est supprimé et ne peut plus vous contacter.\n" +
            "• Le blocage supprime aussi l'historique de chat.\n" +
            "• L'icône corbeille pour le mode suppression.\n" +
            "• Supprimer un ami supprime aussi tous les messages.\n" +
            "• Le bouton recherche pour chercher par nom ou ID.\n" +
            "• Les notifications push sont désactivées par défaut — activez-les dans les paramètres.\n",
    UiTextKey.FriendsEmptyTitle to "Aucun ami",
    UiTextKey.FriendsEmptyMessage to "Appuyez sur « Ajouter un ami » pour chercher par nom ou ID.\n",
    UiTextKey.FriendsInfoGotItButton to "Compris",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Page Apprentissage",
    UiTextKey.LearningInfoMessage to "• Tirez pour actualiser le nombre d'enregistrements.\n" +
            "• Chaque carte affiche la langue et le nombre.\n" +
            "• Appuyez sur « Générer » pour créer une fiche (gratuit la 1re fois).\n" +
            "• La régénération nécessite au moins 5 enregistrements de plus.\n" +
            "• Le bouton fiche ouvre le matériel généré.\n" +
            "• Après la fiche, vous pouvez aussi passer le quiz.",
    UiTextKey.LearningEmptyTitle to "Aucun historique de traduction",
    UiTextKey.LearningEmptyMessage to "Commencez à traduire pour créer des enregistrements.\n" +
            "Les fiches sont générées à partir de l'historique.\n" +
            "Tirez vers le bas pour actualiser après la traduction.",
    UiTextKey.LearningInfoGotItButton to "Compris",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Page Banque de mots",
    UiTextKey.WordBankInfoMessage to "• Tirez pour actualiser la liste des langues.\n" +
            "• Sélectionnez une langue pour voir ou générer la banque.\n" +
            "• La banque est générée à partir de l'historique.\n" +
            "• La mise à jour nécessite au moins 20 enregistrements de plus.\n" +
            "• Ajoutez des mots personnalisés manuellement.\n" +
            "• Partagez des mots avec vos amis.",
    UiTextKey.WordBankInfoGotItButton to "Compris",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Boîte de partage",
    UiTextKey.ShareInboxInfoMessage to "• Tirez pour actualiser la boîte.\n" +
            "• Les éléments partagés par vos amis apparaissent ici.\n" +
            "• Acceptez les mots pour les ajouter ou ignorez-les.\n" +
            "• Appuyez sur « Voir » pour les fiches et quiz.\n" +
            "• Le point rouge (●) indique des éléments nouveaux/non lus.\n" +
            "• Une confirmation est demandée avant d'ignorer un mot partagé.",
    UiTextKey.ShareInboxInfoGotItButton to "Compris",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibilité du profil",
    UiTextKey.MyProfileVisibilityPublic to "Public",
    UiTextKey.MyProfileVisibilityPrivate to "Privé",
    UiTextKey.MyProfileVisibilityDescription to "Public : tout le monde peut vous trouver et vous ajouter.\nPrivé : vous n'apparaissez pas dans les recherches.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Ignorer le mot",
    UiTextKey.ShareDismissWordMessage to "Ignorer ce mot partagé ? Irréversible.",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Langue : {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Fermer",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Connecté ou en attente",
    UiTextKey.AccessibilityNewMessages to "Nouveaux messages",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicateur de nouveaux éléments",
    UiTextKey.AccessibilitySuccessIcon to "Succès",
    UiTextKey.AccessibilityErrorIcon to "Erreur",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Type d'élément partagé",
    UiTextKey.AccessibilityAddCustomWords to "Ajouter des mots personnalisés",
    UiTextKey.AccessibilityWordBankExists to "Banque de mots existante",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.Commentaires",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Paramètres notif.",
    UiTextKey.FriendsNotifSettingsTitle to "Paramètres de notifications",
    UiTextKey.FriendsNotifNewMessages to "Nouveaux messages de chat",
    UiTextKey.FriendsNotifFriendRequests to "Demandes d'amis reçues",
    UiTextKey.FriendsNotifRequestAccepted to "Demande d'ami acceptée",
    UiTextKey.FriendsNotifSharedInbox to "Nouveaux éléments partagés",
    UiTextKey.FriendsNotifCloseButton to "Terminé",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Badges dans l'app (points rouges)",
    UiTextKey.InAppBadgeMessages to "Badge messages non lus",
    UiTextKey.InAppBadgeFriendRequests to "Badge demandes d'amis",
    UiTextKey.InAppBadgeSharedInbox to "Badge boîte de partage non lue",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Veuillez vous connecter pour continuer.",
    UiTextKey.ErrorSaveFailedRetry to "Échec de l'enregistrement. Veuillez réessayer.",
    UiTextKey.ErrorLoadFailedRetry to "Échec du chargement. Veuillez réessayer.",
    UiTextKey.ErrorNetworkRetry to "Erreur réseau. Vérifiez votre connexion.",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Il manque {needed} traductions pour générer le matériel",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Passer en conversation en direct →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Effacer le chat",
    UiTextKey.ChatClearConversationTitle to "Effacer la conversation",
    UiTextKey.ChatClearConversationMessage to "Masquer tous les messages de cette conversation ? Ils resteront masqués même après réouverture. L'autre personne n'est pas affectée.",
    UiTextKey.ChatClearConversationConfirm to "Tout effacer",
    UiTextKey.ChatClearConversationSuccess to "Conversation effacée",

    // Block user
    UiTextKey.BlockUserButton to "Bloquer",
    UiTextKey.BlockUserTitle to "Bloquer cet utilisateur ?",
    UiTextKey.BlockUserMessage to "Bloquer {username} ? Il sera supprimé de votre liste et ne pourra plus vous contacter.",
    UiTextKey.BlockUserConfirm to "Bloquer",
    UiTextKey.BlockUserSuccess to "Utilisateur bloqué et supprimé des amis.",
    UiTextKey.BlockedUsersTitle to "Utilisateurs bloqués",
    UiTextKey.BlockedUsersEmpty to "Aucun utilisateur bloqué.",
    UiTextKey.UnblockUserButton to "Débloquer",
    UiTextKey.UnblockUserTitle to "Débloquer ?",
    UiTextKey.UnblockUserMessage to "Débloquer {username} ? Il pourra à nouveau vous envoyer des demandes.",
    UiTextKey.UnblockUserSuccess to "Utilisateur débloqué.",
    UiTextKey.BlockedUsersManageButton to "Gérer les bloqués",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Note de demande (optionnel)",
    UiTextKey.FriendsRequestNotePlaceholder to "Ajouter une courte note...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Fiche d'apprentissage prête ! Appuyez pour ouvrir.",
    UiTextKey.GenerationBannerWordBank to "Banque de mots prête ! Appuyez pour voir.",
    UiTextKey.GenerationBannerQuiz to "Quiz prêt ! Appuyez pour commencer.",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Notifications",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Chinois (traditionnel)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Système d'amis",
    UiTextKey.HelpFriendSystemBody to "• Cherchez des amis par nom ou ID\n" +
            "• Envoyez, acceptez ou refusez des demandes\n" +
            "• Chattez en temps réel avec traduction\n" +
            "• Partagez mots et matériel d'apprentissage\n" +
            "• Gérez les partages dans la boîte de réception\n" +
            "• Le point rouge (●) indique des messages non lus ou du nouveau contenu\n" +
            "• Tirez vers le bas pour actualiser",
    UiTextKey.HelpProfileVisibilityTitle to "Visibilité du profil",
    UiTextKey.HelpProfileVisibilityBody to "• Profil public ou privé dans les paramètres\n" +
            "• Public : tout le monde peut vous trouver\n" +
            "• Privé : invisible dans les recherches\n" +
            "• Même en privé, partagez votre ID pour être ajouté",
    UiTextKey.HelpColorPalettesTitle to "Thèmes et pièces",
    UiTextKey.HelpColorPalettesBody to "• 1 thème gratuit : Bleu Ciel (par défaut)\n" +
            "• 10 thèmes déverrouillables, 10 pièces chacun\n" +
            "• Gagnez des pièces en complétant des quiz\n" +
            "• Les pièces servent à débloquer des thèmes et à étendre l'historique\n" +
            "• Thème auto : clair 6h–18h, sombre 18h–6h",
    UiTextKey.HelpPrivacyTitle to "Confidentialité et données",
    UiTextKey.HelpPrivacyBody to "• L'audio sert uniquement à la reconnaissance, aucun stockage permanent\n" +
            "• L'OCR est traité sur l'appareil (confidentialité)\n" +
            "• Supprimez votre compte et toutes les données à tout moment\n" +
            "• En mode privé, vous n'apparaissez pas dans les recherches\n" +
            "• Toutes les données sont synchronisées de manière sécurisée via Firebase",
    UiTextKey.HelpAppVersionTitle to "Version de l'app",
    UiTextKey.HelpAppVersionNotes to "• Limite d'historique : 30 à 60 entrées (extensible avec des pièces)\n" +
            "• Le nom d'utilisateur est unique — le changer libère l'ancien\n" +
            "• Déconnexion automatique lors des mises à jour de sécurité\n" +
            "• Toutes les traductions par Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Traduction instantanée",
    UiTextKey.OnboardingPage1Desc to "Traduction rapide pour les phrases courtes, conversation en direct pour le dialogue bidirectionnel.",
    UiTextKey.OnboardingPage2Title to "Apprentissage du vocabulaire",
    UiTextKey.OnboardingPage2Desc to "Générez des fiches de vocabulaire et des quiz à partir de votre historique.",
    UiTextKey.OnboardingPage3Title to "Connectez-vous avec vos amis",
    UiTextKey.OnboardingPage3Desc to "Chattez, partagez des mots et apprenez ensemble.",
    UiTextKey.OnboardingSkipButton to "Passer",
    UiTextKey.OnboardingNextButton to "Suivant",
    UiTextKey.OnboardingGetStartedButton to "Commencer",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Content de vous revoir, {name} !",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Nom d'utilisateur :",
    UiTextKey.ChatUserIdLabel to "ID utilisateur :",
    UiTextKey.ChatLearningLabel to "Apprentissage :",
    UiTextKey.ChatBlockedMessage to "Vous ne pouvez pas envoyer de message à cet utilisateur.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Rechercher",
    UiTextKey.CustomWordsEmptyState to "Aucun mot personnalisé",
    UiTextKey.CustomWordsEmptyHint to "Appuyez sur + pour ajouter un mot",
    UiTextKey.CustomWordsNoSearchResults to "Aucun mot ne correspond",
    UiTextKey.AddCustomWordHintTemplate to "Entrez le mot en {from} et la traduction en {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} enregistrements",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID : {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "E-mail : {email}",
    UiTextKey.ProfileUsernameHintFull to "Nom d'utilisateur pour vos amis (3–20 car., alphanumérique/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Aucune option vocale pour cette langue",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "L'app a été mise à jour. Veuillez vous reconnecter",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Limite de favoris atteinte",
    UiTextKey.FavoritesLimitMessage to "Maximum 20 favoris. Supprimez-en un pour en ajouter un nouveau.",
    UiTextKey.FavoritesLimitGotIt to "Compris",
    UiTextKey.FavoritesInfoTitle to "Infos favoris",
    UiTextKey.FavoritesInfoMessage to "Maximum 20 favoris (enregistrements et conversations). Cette limite réduit la charge de la base de données. Supprimez-en un pour en ajouter.",
    UiTextKey.FavoritesInfoGotIt to "Compris",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Impossible de changer la langue",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "La langue principale ne peut être changée que tous les 30 jours. Veuillez attendre encore {days} jour(s).",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "La langue principale ne peut être changée que tous les 30 jours. Veuillez attendre encore {hours} heure(s).",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Confirmer le changement de langue",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Changer la langue principale vous empêchera de la modifier pendant 30 jours. Continuer ?",

    // Bottom navigation
    UiTextKey.NavHome to "Accueil",
    UiTextKey.NavTranslate to "Traduire",
    UiTextKey.NavLearn to "Apprendre",
    UiTextKey.NavFriends to "Amis",
    UiTextKey.NavSettings to "Paramètres",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Autorisation de la caméra requise",
    UiTextKey.CameraPermissionMessage to "Veuillez autoriser l'accès à la caméra pour la reconnaissance de texte.",
    UiTextKey.CameraPermissionGrant to "Autoriser",
    UiTextKey.MicPermissionMessage to "L'accès au micro est nécessaire pour la reconnaissance vocale.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Supprimer les {count} éléments sélectionnés ? Irréversible.",
    UiTextKey.WordBankDeleteConfirm to "Supprimer « {word} » ?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Tout accepter",
    UiTextKey.FriendsRejectAllButton to "Tout refuser",
    UiTextKey.ChatBlockedCannotSend to "Impossible d'envoyer le message",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Déverrouiller {name} ?",
    UiTextKey.ShopUnlockCost to "Coût : {cost} pièces",
    UiTextKey.ShopYourCoins to "Mes pièces : {coins}",
    UiTextKey.ShopUnlockButton to "Déverrouiller",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Langue principale",
    UiTextKey.HelpPrimaryLanguageBody to "• La langue principale est utilisée pour les explications d'apprentissage\n" +
            "• Modifiable uniquement tous les 30 jours pour la cohérence\n" +
            "• Changez-la dans les paramètres\n" +
            "• Paramètre global pour toutes les pages",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Astuce : pour une meilleure reconnaissance, réglez « Langue source » sur la langue du texte à scanner.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Impossible de changer le nom",
    UiTextKey.SettingsUsernameCooldownMessage to "Le nom d'utilisateur ne peut être changé que tous les 30 jours. Veuillez attendre encore {days} jour(s).",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Le nom d'utilisateur ne peut être changé que tous les 30 jours. Veuillez attendre encore {hours} heure(s).",
    UiTextKey.SettingsUsernameConfirmTitle to "Confirmer le changement de nom",
    UiTextKey.SettingsUsernameConfirmMessage to "Changer votre nom vous empêchera de le modifier pendant 30 jours. Continuer ?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Pas de connexion internet. Vérifiez votre connexion.",
    UiTextKey.ErrorPermissionDenied to "Vous n'avez pas la permission pour cette action.",
    UiTextKey.ErrorSessionExpired to "Session expirée. Veuillez vous reconnecter.",
    UiTextKey.ErrorItemNotFound to "Élément introuvable. Il a peut-être été supprimé.",
    UiTextKey.ErrorAccessDenied to "Accès refusé.",
    UiTextKey.ErrorAlreadyFriends to "Vous êtes déjà amis avec cet utilisateur.",
    UiTextKey.ErrorUserBlocked to "Impossible de compléter l'action. L'utilisateur est peut-être bloqué.",
    UiTextKey.ErrorRequestNotFound to "Cette demande d'ami n'existe plus.",
    UiTextKey.ErrorRequestAlreadyHandled to "Cette demande a déjà été traitée.",
    UiTextKey.ErrorNotAuthorized to "Vous n'êtes pas autorisé à effectuer cette action.",
    UiTextKey.ErrorRateLimited to "Trop de requêtes. Veuillez réessayer plus tard.",
    UiTextKey.ErrorInvalidInput to "Saisie invalide. Vérifiez et réessayez.",
    UiTextKey.ErrorOperationNotAllowed to "Cette opération n'est pas autorisée actuellement.",
    UiTextKey.ErrorTimeout to "L'opération a expiré. Veuillez réessayer.",
    UiTextKey.ErrorSendMessageFailed to "Échec de l'envoi du message. Veuillez réessayer.",
    UiTextKey.ErrorFriendRequestSent to "Demande d'ami envoyée !",
    UiTextKey.ErrorFriendRequestFailed to "Échec de l'envoi de la demande.",
    UiTextKey.ErrorFriendRemoved to "Ami supprimé.",
    UiTextKey.ErrorFriendRemoveFailed to "Échec de la suppression. Vérifiez votre connexion.",
    UiTextKey.ErrorBlockSuccess to "Utilisateur bloqué.",
    UiTextKey.ErrorBlockFailed to "Échec du blocage. Veuillez réessayer.",
    UiTextKey.ErrorUnblockSuccess to "Utilisateur débloqué.",
    UiTextKey.ErrorUnblockFailed to "Échec du déblocage. Veuillez réessayer.",
    UiTextKey.ErrorAcceptRequestSuccess to "Demande d'ami acceptée !",
    UiTextKey.ErrorAcceptRequestFailed to "Échec de l'acceptation. Veuillez réessayer.",
    UiTextKey.ErrorRejectRequestSuccess to "Demande d'ami refusée.",
    UiTextKey.ErrorRejectRequestFailed to "Échec du refus. Veuillez réessayer.",
    UiTextKey.ErrorOfflineMessage to "Vous êtes hors ligne. Certaines fonctionnalités peuvent être indisponibles.",
    UiTextKey.ErrorChatDeletionFailed to "Échec de la suppression du chat. Veuillez réessayer.",
    UiTextKey.ErrorGenericRetry to "Une erreur est survenue. Veuillez réessayer.",
)
