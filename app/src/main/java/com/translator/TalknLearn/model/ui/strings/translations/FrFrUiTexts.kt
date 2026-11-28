package com.translator.TalknLearn.model.ui

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

    // Pagination
    UiTextKey.PaginationPrevLabel to "Page précédente",
    UiTextKey.PaginationNextLabel to "Page suivante",
    UiTextKey.PaginationPageLabelTemplate to "Page {page} sur {total}",

    // Toast
    UiTextKey.DisableText to "Connectez-vous pour utiliser la traduction et enregistrer l'historique.",

    // Error
    UiTextKey.ErrorRetryButton to "Réessayer",

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
        UiTextKey.AuthGoogleSignInButton to "Se connecter avec Google",
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
    UiTextKey.SettingsColorCostTemplate to "{cost} pièces",
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
    UiTextKey.LearningRegenInfoTitle to "Règles de régénération",
    UiTextKey.LearningRegenInfoMessage to "Pour régénérer le matériel d'apprentissage :\n\n• Première génération : à tout moment\n• Régénération : au moins 5 enregistrements de plus\n\nLe bouton devient bleu quand il y a assez d'enregistrements. S'il est gris, continuez à traduire !\n\n💡 Astuce : si le nombre ne se met pas à jour, redémarrez l'app.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz : {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Générer un quiz",
    UiTextKey.QuizGenerating to "⏳ Génération...",
    UiTextKey.QuizBlocked to "🚫 Bloqué",
    UiTextKey.QuizWait to "⏳ Patientez...",
    UiTextKey.QuizCanEarnCoins to "🪙 Vous pouvez gagner des pièces !",
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
    UiTextKey.QuizRecordsLabel to "enregistrements",

    // History coins

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
    UiTextKey.WordBankRefresh to "🔄 Mettre à jour la banque",
    UiTextKey.WordBankEmpty to "Banque de mots vide",
    UiTextKey.WordBankEmptyHint to "Appuyez sur le bouton ci-dessus pour générer à partir de votre historique.",
    UiTextKey.WordBankExample to "Exemple :",
    UiTextKey.WordBankDifficulty to "Difficulté :",
    UiTextKey.WordBankFilterCategory to "Catégorie",
    UiTextKey.WordBankFilterCategoryAll to "Toutes les catégories",
    UiTextKey.WordBankFilterDifficultyLabel to "Difficulté :",
    UiTextKey.WordBankFilterNoResults to "Aucun mot ne correspond aux filtres",
    UiTextKey.WordBankRegenInfoTitle to "Règles de mise à jour",
    UiTextKey.WordBankRegenInfoMessage to "Pour mettre à jour la banque de mots :\n\n• Première génération : à tout moment\n• Mise à jour : au moins 20 enregistrements de plus\n\nLe bouton devient bleu quand il y en a assez. S'il est gris, continuez à traduire !\n\n💡 Astuce : si le nombre ne se met pas à jour, redémarrez l'app.",

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

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Supprimer le compte",
    UiTextKey.AccountDeleteWarning to "⚠️ Cette action est permanente et irréversible !",
    UiTextKey.AccountDeleteConfirmMessage to "Toutes les données seront supprimées définitivement : historique, banque de mots, matériel d'apprentissage, paramètres. Entrez votre mot de passe pour confirmer.",
    UiTextKey.AccountDeletePasswordLabel to "Mot de passe",
        UiTextKey.AccountDeleteButton to "Supprimer mon compte et mes données",

    // Favorites
    UiTextKey.FavoritesTitle to "Favoris",
    UiTextKey.FavoritesEmpty to "Aucun favori",
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
    UiTextKey.CustomWordsOriginalLanguageLabel to "Langue d'origine",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Langue de traduction",
    UiTextKey.CustomWordsSaveButton to "Enregistrer",
    UiTextKey.CustomWordsCancelButton to "Annuler",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Détection auto",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Scanner du texte depuis une image",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Attention : la reconnaissance de texte par image peut ne pas être totalement exacte. Vérifiez le texte extrait." +
            "Supporte le latin (anglais, etc.), le chinois, le japonais et le coréen.",

    // Cache

    // Auto theme
    UiTextKey.SettingsAutoThemePreview to "Le thème changera automatiquement aux heures définies",

    // Offline mode
    UiTextKey.OfflineSyncPending to "Les modifications seront synchronisées en ligne",

    // Image capture
    UiTextKey.ImageSourceTitle to "Choisir la source de l'image",
    UiTextKey.ImageSourceCamera to "Prendre une photo",
    UiTextKey.ImageSourceGallery to "Choisir dans la galerie",
    UiTextKey.ImageSourceCancel to "Annuler",
    UiTextKey.CameraCaptureContentDesc to "Capturer",

    // Friends
    UiTextKey.FriendsTitle to "Amis",
    UiTextKey.FriendsAddButton to "Ajouter un ami",
    UiTextKey.FriendsSearchTitle to "Rechercher un utilisateur",
    UiTextKey.FriendsSearchPlaceholder to "Nom d'utilisateur ou ID...",
    UiTextKey.FriendsSearchNoResults to "Aucun utilisateur trouvé",
    UiTextKey.FriendsRequestsSection to "Demandes d'amis ({count})",
    UiTextKey.FriendsSectionTitle to "Amis ({count})",
    UiTextKey.FriendsAcceptButton to "Accepter",
    UiTextKey.FriendsRejectButton to "Refuser",
    UiTextKey.FriendsSendRequestButton to "Ajouter",
    UiTextKey.FriendsCloseButton to "Fermer",
    UiTextKey.FriendsCancelButton to "Annuler",
    UiTextKey.FriendsRemoveConfirm to "Supprimer",
    UiTextKey.FriendsNewRequestsTemplate to "Vous avez {count} nouvelle(s) demande(s) d'ami !",
    UiTextKey.FriendsSentRequestsSection to "Demandes envoyées ({count})",
    UiTextKey.FriendsPendingStatus to "En attente",
    UiTextKey.FriendsCancelRequestButton to "Annuler la demande",
    UiTextKey.FriendsUnreadMessageDesc to "Envoyer un message",
    UiTextKey.FriendsDeleteModeButton to "Supprimer des amis",
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
    UiTextKey.ChatUnreadBadge to "{count} non lu(s)",
    UiTextKey.ChatTranslateButton to "Traduire",
    UiTextKey.ChatTranslateDialogTitle to "Traduire la conversation",
    UiTextKey.ChatTranslateDialogMessage to "Traduire les messages de votre ami dans votre langue ? La langue de chaque message sera détectée et traduite.",
    UiTextKey.ChatTranslateConfirm to "Tout traduire",
    UiTextKey.ChatShowOriginal to "Afficher l'original",
    UiTextKey.ChatShowTranslation to "Afficher la traduction",
    UiTextKey.ChatTranslatedLabel to "Traduit",

    // Sharing
    UiTextKey.ShareInboxTitle to "Boîte de partage",
    UiTextKey.ShareInboxEmpty to "Aucun partage. Vos amis peuvent partager des mots et du matériel !",
    UiTextKey.ShareWordButton to "Partager le mot",
    UiTextKey.ShareMaterialButton to "Partager le matériel",
    UiTextKey.ShareSelectFriendTitle to "Choisir un ami",
    UiTextKey.ShareSelectFriendMessage to "Sélectionnez un ami pour partager :",
    UiTextKey.ShareSuccess to "Partagé avec succès !",
    UiTextKey.ShareAcceptButton to "Accepter",
    UiTextKey.ShareDismissButton to "Ignorer",
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
    UiTextKey.MyProfileCopyUserId to "Copier l'ID",
    UiTextKey.MyProfileCopyUsername to "Copier le nom",
    UiTextKey.MyProfileShare to "Partager le profil",
    UiTextKey.MyProfileCopied to "Copié dans le presse-papiers !",
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

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Fermer",
    UiTextKey.AccessibilityNewMessages to "Nouveaux messages",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicateur de nouveaux éléments",
    UiTextKey.AccessibilitySuccessIcon to "Succès",
    UiTextKey.AccessibilityErrorIcon to "Erreur",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Type d'élément partagé",
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

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Badges dans l'app (points rouges)",
    UiTextKey.InAppBadgeMessages to "Badge messages non lus",
    UiTextKey.InAppBadgeFriendRequests to "Badge demandes d'amis",
    UiTextKey.InAppBadgeSharedInbox to "Badge boîte de partage non lue",

    // Error messages

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Il manque {needed} traductions pour générer le matériel",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Passer en conversation en direct →",

    // Chat clear
    UiTextKey.ChatClearConversationTitle to "Effacer la conversation",
    UiTextKey.ChatClearConversationMessage to "Masquer tous les messages de cette conversation ? Ils resteront masqués même après réouverture. L'autre personne n'est pas affectée.",
    UiTextKey.ChatClearConversationConfirm to "Tout effacer",
    UiTextKey.ChatClearConversationSuccess to "Conversation effacée",

    // Block user
    UiTextKey.BlockUserButton to "Bloquer",
    UiTextKey.BlockUserTitle to "Bloquer cet utilisateur ?",
    UiTextKey.BlockUserMessage to "Bloquer {username} ? Il sera supprimé de votre liste et ne pourra plus vous contacter.",
    UiTextKey.BlockUserConfirm to "Bloquer",
    UiTextKey.BlockedUsersTitle to "Utilisateurs bloqués",
    UiTextKey.BlockedUsersEmpty to "Aucun utilisateur bloqué.",
    UiTextKey.UnblockUserButton to "Débloquer",
    UiTextKey.UnblockUserTitle to "Débloquer ?",
    UiTextKey.UnblockUserMessage to "Débloquer {username} ? Il pourra à nouveau vous envoyer des demandes.",
    UiTextKey.BlockedUsersManageButton to "Gérer les bloqués",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Note de demande (optionnel)",
    UiTextKey.FriendsRequestNotePlaceholder to "Ajouter une courte note...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Fiche d'apprentissage prête ! Appuyez pour ouvrir.",
    UiTextKey.GenerationBannerWordBank to "Banque de mots prête ! Appuyez pour voir.",
    UiTextKey.GenerationBannerQuiz to "Quiz prêt ! Appuyez pour commencer.",

    // Notification settings quick link

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

    // Phase 1 fixes
    UiTextKey.SettingsPasswordManagedByGoogle to "Mot de passe géré par Google",
    UiTextKey.SpeechListeningLabel to "Écoute… parlez maintenant",
    UiTextKey.QuizLeaveTitle to "Quitter le quiz ?",
    UiTextKey.QuizLeaveMessage to "Vos réponses seront perdues.",
    UiTextKey.QuizLeaveConfirm to "Quitter",
    UiTextKey.QuizLeaveStay to "Rester",

    // Phase 1 part 2
    UiTextKey.A11yAddFavorite to "Ajouter aux favoris",
    UiTextKey.A11yRemoveFavorite to "Retirer des favoris",
    UiTextKey.A11yViewFriendProfile to "Voir le profil de l'ami",
    UiTextKey.A11yRefreshAutoDetect to "Relancer la détection auto",
    UiTextKey.A11yInstructions to "Instructions",
    UiTextKey.A11yClearText to "Effacer le texte",
    UiTextKey.A11yAddCustomWords to "Ajouter des mots personnalisés",
    UiTextKey.A11yFilter to "Filtrer",
    UiTextKey.A11ySpeakOriginal to "Lire l'original",
    UiTextKey.A11ySpeakTranslation to "Lire la traduction",
    UiTextKey.A11ySpeakExample to "Lire l'exemple",
    UiTextKey.A11yCoinRules to "Règles des pièces",
    UiTextKey.StatusWaiting to "En attente…",
    UiTextKey.ErrorNotLoggedIn to "Non connecté",
    UiTextKey.HistoryErrorDeleteFailed to "Échec de la suppression. Veuillez réessayer.",
    UiTextKey.HistoryErrorRenameFailed to "Échec du renommage. Veuillez réessayer.",
    UiTextKey.HistoryErrorDeleteSessionFailed to "Impossible de supprimer la session. Veuillez réessayer.",
    UiTextKey.HistoryErrorLoadMoreFailed to "Impossible de charger plus d'enregistrements.",
    UiTextKey.HistoryErrorRemoveFavoriteFailed to "Impossible de retirer des favoris.",
    UiTextKey.HistoryErrorAddFavoriteFailed to "Impossible d'ajouter aux favoris.",
    UiTextKey.HistoryErrorFavoriteSessionFailed to "Impossible d'enregistrer la session dans les favoris.",
    UiTextKey.HistoryErrorUnfavoriteSessionFailed to "Impossible de retirer la session des favoris.",
    UiTextKey.TtsSpeaking to "Lecture…",
    UiTextKey.TtsSpeakingTranslation to "Lecture de la traduction…",
    UiTextKey.TtsSpoken to "✓ Lu",
    UiTextKey.TtsError to "✗ {detail}",
    UiTextKey.SpeechStatusRecognitionError to "Erreur de reconnaissance : {detail}",
    UiTextKey.SpeechStatusAutoDetecting to "Détection automatique de la langue…",
    UiTextKey.SpeechStatusDetected to "Détecté : {language}",
    UiTextKey.SpeechStatusDetectedConfidence to "Détecté : {language} (confiance {confidence} %)",
    UiTextKey.SpeechStatusEnterText to "Saisissez ou dites le texte à traduire.",
    UiTextKey.SpeechStatusLoginRequired to "Connexion requise pour utiliser la traduction.",
    UiTextKey.SpeechStatusDetectFailed to "Langue source non détectée. Veuillez la choisir manuellement.",
    UiTextKey.SpeechStatusTranslationError to "Erreur de traduction : {detail}",
    UiTextKey.SpeechStatusScanningImage to "Analyse du texte de l'image…",
    UiTextKey.SpeechStatusTextExtracted to "Texte extrait avec succès",
    UiTextKey.SpeechStatusOcrError to "Erreur d'analyse de l'image : {detail}",
    UiTextKey.SpeechStatusEnterTextFirst to "Saisissez ou dites d'abord un texte.",
    UiTextKey.SpeechStatusDetectFailedForSpeech to "Langue source de la parole non détectée. Veuillez choisir une langue source.",
    UiTextKey.SpeechStatusRefreshed to "Actualisé. La détection automatique sera relancée à la prochaine traduction ou lecture.",
    UiTextKey.ContinuousStatusLoginRequired to "Connectez-vous pour utiliser la traduction continue.",
    UiTextKey.ContinuousStatusPreparingMic to "Préparation du micro…",
    UiTextKey.ContinuousStatusTranslationError to "Erreur de traduction continue : {detail}",
    UiTextKey.ContinuousStatusRateLimited to "{detail} Le mode continu a été arrêté pour éviter des échecs répétés.",
    UiTextKey.ContinuousStatusRecognitionError to "Erreur de reconnaissance continue : {detail}",
    UiTextKey.ContinuousStatusStartError to "Impossible de démarrer le mode continu : {detail}",

    // Phase 1 part 3: social area messages
    UiTextKey.StatusRaw to "{detail}",
    UiTextKey.ErrGeneric to "Un problème est survenu. Veuillez réessayer.",
    UiTextKey.ErrNoInternet to "Pas de connexion Internet. Vérifiez votre réseau et réessayez.",
    UiTextKey.ErrPermissionDenied to "Vous n'avez pas l'autorisation d'effectuer cette action.",
    UiTextKey.ErrSessionExpired to "Votre session a expiré. Veuillez vous reconnecter.",
    UiTextKey.ErrItemNotFound to "L'élément demandé est introuvable. Il a peut-être été supprimé.",
    UiTextKey.ErrAccessDenied to "Accès refusé.",
    UiTextKey.ErrAlreadyFriends to "Vous êtes déjà ami avec cet utilisateur.",
    UiTextKey.ErrUserMaybeBlocked to "Impossible d'effectuer cette action. L'utilisateur est peut-être bloqué.",
    UiTextKey.ErrRequestGone to "Cette demande d'ami n'existe plus.",
    UiTextKey.ErrRequestHandled to "Cette demande a déjà été traitée par quelqu'un d'autre.",
    UiTextKey.ErrNotAuthorized to "Vous n'êtes pas autorisé à effectuer cette action.",
    UiTextKey.ErrTooManyRequests to "Trop de requêtes. Patientez un instant et réessayez.",
    UiTextKey.ErrInvalidInput to "Saisie non valide. Vérifiez et réessayez.",
    UiTextKey.ErrCannotCompleteNow to "Impossible d'effectuer cette opération pour le moment.",
    UiTextKey.ErrTimedOut to "L'opération a expiré. Veuillez réessayer.",
    UiTextKey.FriendsMsgRemoved to "Ami supprimé.",
    UiTextKey.FriendsMsgRemoveFailed to "Impossible de supprimer l'ami. Vérifiez votre connexion et réessayez.",
    UiTextKey.FriendsMsgRemovedMany to "Ami(s) supprimé(s).",
    UiTextKey.FriendsMsgNeedUsername to
        "Définissez un nom d'utilisateur dans votre profil avant de gérer les demandes d'ami.",
    UiTextKey.FriendsMsgMaxPending to
        "Vous avez atteint le maximum de {limit} demandes d'ami en attente. Attendez que certaines soient " +
            "acceptées ou annulez-en avant d'en envoyer d'autres.",
    UiTextKey.FriendsMsgRateLimit to
        "Limite atteinte. Vous pouvez envoyer jusqu'à {limit} demandes d'ami par heure. Réessayez dans environ " +
            "{minutes} minute(s).",
    UiTextKey.FriendsMsgNoteTooLong to "La note ne doit pas dépasser {max} caractères",
    UiTextKey.FriendsMsgRequestSent to "Demande d'ami envoyée ! La personne sera notifiée.",
    UiTextKey.FriendsMsgAlreadyFriends to "Vous êtes déjà ami avec cet utilisateur.",
    UiTextKey.FriendsMsgRequestPending to
        "Vous avez déjà une demande en attente pour cet utilisateur. Attendez sa réponse.",
    UiTextKey.FriendsMsgProfileMissing to "Profil de l'utilisateur introuvable. Il a peut-être supprimé son compte.",
    UiTextKey.FriendsMsgYouBlocked to "Vous avez bloqué cet utilisateur. Débloquez-le d'abord.",
    UiTextKey.FriendsMsgUnableToSend to "Impossible d'envoyer la demande d'ami.",
    UiTextKey.FriendsMsgSendFailed to "Échec de l'envoi de la demande d'ami. Veuillez réessayer.",
    UiTextKey.FriendsMsgAccepted to "Demande d'ami acceptée ! Vous êtes maintenant amis.",
    UiTextKey.FriendsMsgAcceptGone to "Cette demande n'existe plus — elle a peut-être été annulée.",
    UiTextKey.FriendsMsgAcceptNotAuthorized to "Vous n'êtes pas autorisé à accepter cette demande.",
    UiTextKey.FriendsMsgAcceptFailed to "Échec de l'acceptation de la demande. Veuillez réessayer.",
    UiTextKey.FriendsMsgDeclined to "Demande refusée.",
    UiTextKey.FriendsMsgDeclineGone to "Cette demande n'existe plus.",
    UiTextKey.FriendsMsgDeclineFailed to "Échec du refus de la demande. Veuillez réessayer.",
    UiTextKey.FriendsMsgCancelled to "Demande d'ami annulée.",
    UiTextKey.FriendsMsgCancelGone to "Cette demande n'existe plus — elle a peut-être déjà été acceptée ou refusée.",
    UiTextKey.FriendsMsgCancelFailed to "Échec de l'annulation de la demande. Veuillez réessayer.",
    UiTextKey.FriendsMsgBatchAcceptAll to "Les {count} demandes d'ami ont toutes été acceptées !",
    UiTextKey.FriendsMsgBatchAcceptFailed to "Échec de l'acceptation des demandes. Veuillez réessayer.",
    UiTextKey.FriendsMsgBatchAcceptMixed to
        "{success} acceptée(s), {failed} en échec. Réessayez pour celles qui ont échoué.",
    UiTextKey.FriendsMsgBatchAcceptStoppedAt to "Acceptation groupée annulée après {processed}/{total} demande(s).",
    UiTextKey.FriendsMsgBatchAcceptStopped to "Acceptation groupée annulée.",
    UiTextKey.FriendsMsgBatchDeclined to "{count} demande(s) refusée(s).",
    UiTextKey.FriendsMsgBatchDeclineStoppedAt to "Refus groupé annulé après {processed}/{total} demande(s).",
    UiTextKey.FriendsMsgBatchDeclineStopped to "Refus groupé annulé.",
    UiTextKey.FriendsMsgBlockedRemoved to "Utilisateur bloqué et retiré de vos amis.",
    UiTextKey.FriendsMsgBlockFailed to "Échec du blocage de l'utilisateur. Veuillez réessayer.",
    UiTextKey.FriendsMsgBlocked to "Utilisateur bloqué.",
    UiTextKey.FriendsMsgUnblocked to "Utilisateur débloqué.",
    UiTextKey.FriendsMsgUnblockFailed to "Échec du déblocage de l'utilisateur. Veuillez réessayer.",
    UiTextKey.ChatMsgLoadFailed to "Échec du chargement des messages. Veuillez réessayer.",
    UiTextKey.ChatMsgBlockFailed to "Échec du blocage de l'utilisateur.",
    UiTextKey.ChatMsgUnblockFailed to "Échec du déblocage de l'utilisateur.",
    UiTextKey.ChatMsgTooLong to "Le message ne doit pas dépasser {max} caractères",
    UiTextKey.ChatMsgTooFast to "Vous envoyez des messages trop vite. Patientez un instant.",
    UiTextKey.ChatMsgSendFailed to "Échec de l'envoi du message",
    UiTextKey.ChatMsgTranslateFailed to "Échec de la traduction. Veuillez réessayer.",
    UiTextKey.MyProfileMsgLoadFailed to "Échec du chargement du profil. Veuillez réessayer.",
    UiTextKey.MyProfileMsgNeedUsername to "Définissez un nom d'utilisateur avant de rendre votre profil public.",
    UiTextKey.MyProfileMsgPublic to "Profil rendu public",
    UiTextKey.MyProfileMsgPrivate to "Profil rendu privé",
    UiTextKey.MyProfileMsgVisibilityFailed to "Échec de la mise à jour de la visibilité. Veuillez réessayer.",
    UiTextKey.SharedMsgAdded to "Ajouté à votre collection",
    UiTextKey.SharedMsgDeleted to "Élément supprimé",
    UiTextKey.SharedMsgDismissed to "Élément ignoré",
    UiTextKey.SharedMsgAcceptFailed to "Échec de l'acceptation de l'élément",
    UiTextKey.SharedMsgDeleteFailed to "Échec de la suppression de l'élément",
    UiTextKey.SharedMsgDismissFailed to "Échec de l'opération pour ignorer l'élément",
    UiTextKey.SharedMsgNoItemId to "Aucun ID d'élément",
    UiTextKey.SharedMsgLoadFailed to "Échec du chargement du contenu partagé. Veuillez réessayer.",

    // Phase 1 part 4: settings, shop, profile, login and feedback messages
    UiTextKey.FeedbackMsgEmpty to "Le message ne peut pas être vide",
    UiTextKey.FeedbackMsgTooShort to "Le commentaire doit contenir au moins {min} caractères",
    UiTextKey.FeedbackMsgTooLong to "Le commentaire ne doit pas dépasser {max} caractères",
    UiTextKey.FeedbackMsgRateLimited to
        "Vous avez envoyé trop de commentaires récemment. Veuillez réessayer plus tard.",
    UiTextKey.FeedbackMsgSubmitFailed to "Échec de l'envoi du commentaire. Veuillez vérifier votre connexion Internet.",
    UiTextKey.ShopMsgLoadFailed to "Impossible de charger les données de la boutique",
    UiTextKey.ShopMsgPurchaseFailed to "Échec de l'achat",
    UiTextKey.ShopMsgHistoryExpanded to "Limite d'historique portée à {limit} entrées !",
    UiTextKey.ShopMsgPaletteAlreadyUnlocked to "Palette déjà débloquée",
    UiTextKey.ShopMsgUnlockFailed to "Impossible de débloquer la palette",
    UiTextKey.ShopMsgPaletteUnlocked to "Palette débloquée !",
    UiTextKey.ShopMsgPaletteSelectFailed to "Impossible de changer la palette de couleurs. Veuillez réessayer.",
    UiTextKey.ProfileMsgLoadFailed to "Impossible de charger le profil",
    UiTextKey.ProfileMsgUsernameTooShort to "Le nom d'utilisateur doit contenir au moins {min} caractères",
    UiTextKey.ProfileMsgUsernameTooLong to "Le nom d'utilisateur ne doit pas dépasser {max} caractères",
    UiTextKey.ProfileMsgUsernameInvalidChars to
        "Le nom d'utilisateur ne peut contenir que des lettres, des chiffres et des traits de soulignement",
    UiTextKey.ProfileMsgVerifyFailed to "Impossible de vérifier l'état du compte. Veuillez réessayer.",
    UiTextKey.ProfileMsgUsernameTaken to "Ce nom d'utilisateur est déjà pris",
    UiTextKey.ProfileMsgUsernameUpdatedSyncing to
        "Nom d'utilisateur mis à jour. Votre liste d'amis peut mettre un moment à s'actualiser.",
    UiTextKey.ProfileMsgUsernameUpdated to "Nom d'utilisateur mis à jour",
    UiTextKey.ProfileMsgUpdateFailed to "Impossible de mettre à jour le profil",
    UiTextKey.ProfileMsgSetUsernameFailed to "Impossible de définir le nom d'utilisateur",
    UiTextKey.ProfileMsgDeleteFailed to "Impossible de supprimer le compte",
    UiTextKey.ProfileMsgInvalidPassword to "Mot de passe incorrect. Veuillez réessayer.",
    UiTextKey.ProfileMsgGoogleReauthFailed to "Échec de la réauthentification Google. Veuillez réessayer.",
    UiTextKey.ProfileMsgGoogleNoToken to "Échec de la connexion Google : aucun jeton d'identification.",
    UiTextKey.ProfileMsgGoogleSignInFailed to "Échec de la connexion Google. Veuillez réessayer.",
    UiTextKey.ProfileGoogleReauthHint to "Réauthentifiez-vous avec votre compte Google pour confirmer.",
    UiTextKey.ProfileContinueWithGoogle to "Continuer avec Google",
    UiTextKey.SettingsMsgLanguageSaveFailed to "Impossible d'enregistrer la langue. Veuillez réessayer.",
    UiTextKey.SettingsMsgFontSaveFailed to "Impossible d'enregistrer la taille du texte. Veuillez réessayer.",
    UiTextKey.SettingsMsgThemeSaveFailed to "Impossible d'enregistrer le thème. Veuillez réessayer.",
    UiTextKey.SettingsMsgAutoThemeFailed to "Impossible de désactiver le thème automatique. Veuillez réessayer.",
    UiTextKey.SettingsMsgVoiceSaveFailed to "Impossible d'enregistrer la voix. Veuillez réessayer.",
    UiTextKey.SettingsMsgNotificationSaveFailed to "Impossible d'enregistrer les notifications. Veuillez réessayer.",
    UiTextKey.AuthMsgEmailEmpty to "L'e-mail ne peut pas être vide",
    UiTextKey.AuthMsgEmailInvalid to "Format d'e-mail invalide",
    UiTextKey.AuthMsgTooManyLogins to "Trop de tentatives de connexion. Veuillez réessayer plus tard.",
    UiTextKey.AuthMsgTooManyResets to
        "Trop de demandes de réinitialisation du mot de passe. Veuillez réessayer plus tard.",
    UiTextKey.AuthMsgGoogleEmptyToken to "Échec de la connexion Google : jeton d'identification vide.",
    UiTextKey.AuthMsgGoogleNoToken to "Échec de la connexion Google : aucun jeton d'identification reçu.",
    UiTextKey.AuthMsgGoogleFailedCode to "Échec de la connexion Google (code {code}).",
    UiTextKey.AuthMsgGoogleNotConfigured to "La connexion Google n'est pas configurée pour cette version.",
    UiTextKey.ErrUnknown to "Une erreur inconnue s'est produite.",
    UiTextKey.ErrNetworkFailed to "Échec de la connexion réseau. Veuillez vérifier votre connexion Internet.",
    UiTextKey.ErrOperationTimedOut to "L'opération a expiré. Veuillez réessayer.",
    UiTextKey.ErrCancelled to "L'opération a été annulée.",
    UiTextKey.ErrUnexpected to "Une erreur inattendue s'est produite. Veuillez réessayer.",
    UiTextKey.ErrAuthInvalidEmail to "L'adresse e-mail est mal formatée.",
    UiTextKey.ErrAuthWrongPassword to "Mot de passe incorrect.",
    UiTextKey.ErrAuthUserNotFound to "Aucun compte trouvé avec cet e-mail.",
    UiTextKey.ErrAuthUserDisabled to "Ce compte a été désactivé.",
    UiTextKey.ErrAuthTooManyRequests to "Trop de demandes. Veuillez réessayer plus tard.",
    UiTextKey.ErrAuthEmailInUse to "Cette adresse e-mail est déjà utilisée par un autre compte.",
    UiTextKey.ErrAuthFailed to "Échec de l'authentification. Veuillez réessayer.",
    UiTextKey.ErrDbPermission to "Vous n'avez pas l'autorisation d'accéder à ces données.",
    UiTextKey.ErrDbUnavailable to "Service actuellement indisponible. Veuillez réessayer plus tard.",
    UiTextKey.ErrDbGeneric to "Erreur de base de données. Veuillez réessayer.",

    // Phase 1 part 5: learning, word bank, custom words, favorites and empty states
    UiTextKey.LearningMsgLoadFailed to "Échec du chargement des fiches d'apprentissage.",
    UiTextKey.LearningMsgSheetLoadFailed to "Échec du chargement de la fiche d'apprentissage.",
    UiTextKey.LearningMsgSheetGenerateCancelled to "Génération de la fiche d'apprentissage annulée.",
    UiTextKey.LearningMsgSheetGenerateFailed to
        "Échec de la génération de la fiche d'apprentissage. Veuillez réessayer.",
    UiTextKey.LearningMsgQuizParseIncomplete to
        "Le quiz ne contient que {parsed} questions sur {total}. Veuillez le régénérer.",
    UiTextKey.LearningMsgQuizGenerateCancelled to "Génération du quiz annulée.",
    UiTextKey.LearningMsgQuizGenerateFailed to "Échec de la génération du quiz. Veuillez réessayer.",
    UiTextKey.LearningMsgShareNotReady to
        "Le contenu de la fiche n'est pas prêt. Veuillez attendre la fin du chargement.",
    UiTextKey.QuizMsgNotGenerated to "Aucun quiz généré pour l'instant.",
    UiTextKey.QuizMsgCorrupted to "Le quiz enregistré est corrompu. Veuillez le régénérer.",
    UiTextKey.QuizMsgLoadFailed to "Échec du chargement du quiz.",
    UiTextKey.QuizMsgSubmitFailed to "Échec de l'envoi du quiz. Veuillez réessayer.",
    UiTextKey.ShareMsgFailed to "Échec du partage. Veuillez réessayer.",
    UiTextKey.WordBankMsgLoadFailed to "Échec du chargement de la banque de mots.",
    UiTextKey.WordBankMsgDeleteFailed to "Échec de la suppression du mot. Veuillez réessayer.",
    UiTextKey.WordBankMsgNeedMoreRecords to "Il faut {count} enregistrements de plus pour actualiser la banque de mots",
    UiTextKey.WordBankMsgNoHistory to "Aucun historique de traduction pour cette langue",
    UiTextKey.WordBankMsgGenerateFailed to "Échec de la génération de la banque de mots. Veuillez réessayer.",
    UiTextKey.WordBankMsgShareIncomplete to "Les données du mot sont incomplètes. Actualisez et réessayez.",
    UiTextKey.CustomWordsMsgOriginalRequired to "Le mot d'origine est obligatoire",
    UiTextKey.CustomWordsMsgOriginalTooLong to "Le mot d'origine ne doit pas dépasser {max} caractères",
    UiTextKey.CustomWordsMsgTranslatedRequired to "Le mot traduit est obligatoire",
    UiTextKey.CustomWordsMsgTranslatedTooLong to "Le mot traduit ne doit pas dépasser {max} caractères",
    UiTextKey.CustomWordsMsgPronunciationTooLong to "La prononciation ne doit pas dépasser {max} caractères",
    UiTextKey.CustomWordsMsgExampleTooLong to "L'exemple ne doit pas dépasser {max} caractères",
    UiTextKey.CustomWordsMsgDuplicate to "Ce mot existe déjà dans votre banque de mots personnalisée",
    UiTextKey.CustomWordsMsgAddFailed to "Échec de l'ajout du mot",
    UiTextKey.CustomWordsMsgInvalidLanguageUpdate to "Demande de mise à jour de langue invalide",
    UiTextKey.CustomWordsMsgTranslateFailed to "Échec de la traduction : {detail}",
    UiTextKey.CustomWordsMsgUpdateLanguageFailed to "Échec de la mise à jour de la langue du mot",
    UiTextKey.CustomWordsMsgLoadFailed to "Échec du chargement des mots personnalisés",
    UiTextKey.FavoritesMsgLoadFailed to "Échec du chargement des favoris",
    UiTextKey.FavoritesMsgLoadMoreFailed to "Échec du chargement de plus de favoris",
    UiTextKey.FavoritesMsgRefreshFailed to "Échec de l'actualisation des favoris",
    UiTextKey.FavoritesMsgRemoveFailed to "Échec de la suppression du favori",
    UiTextKey.FavoritesMsgDeleteSelectedFailed to "Échec de la suppression des éléments sélectionnés",
    UiTextKey.FavoritesLoadMore to "Charger plus",
    UiTextKey.A11yConfirmDelete to "Confirmer la suppression",
    UiTextKey.A11yDeleteMode to "Mode suppression",
    UiTextKey.A11yExpand to "Développer",
    UiTextKey.A11yCollapse to "Réduire",
    UiTextKey.EmptyNoHistoryTitle to "Aucun historique",
    UiTextKey.EmptyNoFavoritesTitle to "Aucun favori",
    UiTextKey.EmptyNoLearningTitle to "Aucun support d'apprentissage",
    UiTextKey.EmptySharedItemsMessage to "Aucun élément partagé avec vous pour l'instant",
    UiTextKey.SharedMaterialLoadingContent to "Chargement du contenu...",
    UiTextKey.HelpVersionTemplate to "Version : {version}",
    UiTextKey.StartupTagline to "Traduire et apprendre",

    // Extended Error Messages
)
