
import sys
import re

def add_key_to_enum(filepath, key_name):
    with open(filepath, 'r') as f:
        content = f.readlines()

    enum_start_pattern = re.compile(r'enum class UiTextKey {')
    last_enum_entry_index = -1

    for i, line in enumerate(content):
        if enum_start_pattern.search(line):
            # Find the last entry before the closing brace or a comment block
            for j in range(i + 1, len(content)):
                if '}' in content[j] or '//' in content[j] or '/*'
                    '*/' in content[j]:
                    last_enum_entry_index = j - 1
                    # Skip empty lines or lines with only whitespace
                    while last_enum_entry_index > i and not content[last_enum_entry_index].strip():
                        last_enum_entry_index -= 1
                    break
            break

    if last_enum_entry_index != -1:
        # Ensure the previous line ends with a comma if it's not a comment or brace
        if not content[last_enum_entry_index].strip().endswith(',') and not content[last_enum_entry_index].strip().endswith('{') and not content[last_enum_entry_index].strip().startswith('//') and not content[last_enum_entry_index].strip().startswith('/*'):
            content[last_enum_entry_index] = content[last_enum_entry_index].rstrip() + ',
'
        content.insert(last_enum_entry_index + 1, f'    {key_name},
')
    else:
        print(f"Error: Could not find suitable insertion point in {filepath}")
        return False

    with open(filepath, 'w') as f:
        f.writelines(content)
    return True

def add_english_text(filepath, key_name, english_value):
    with open(filepath, 'r') as f:
        content = f.readlines()

    base_uitexts_start_pattern = re.compile(r'val BaseUiTexts = mapOf<UiTextKey, String>\(')
    insertion_index = -1

    for i, line in enumerate(content):
        if base_uitexts_start_pattern.search(line):
            # Find the line before the closing parenthesis of the mapOf
            for j in range(i + 1, len(content)):
                if content[j].strip() == ')':
                    insertion_index = j
                    break
            break

    if insertion_index != -1:
        # Ensure the previous line ends with a comma if it's not a comment or brace
        if not content[insertion_index - 1].strip().endswith(',') and not content[insertion_index - 1].strip().endswith('(') and not content[insertion_index - 1].strip().startswith('//') and not content[insertion_index - 1].strip().startswith('/*'):
            content[insertion_index - 1] = content[insertion_index - 1].rstrip() + ',
'
        content.insert(insertion_index, f'    UiTextKey.{key_name} to "{english_value}",
')
    else:
        print(f"Error: Could not find suitable insertion point in {filepath}")
        return False

    with open(filepath, 'w') as f:
        f.writelines(content)
    return True

def add_translated_text(filepath, key_name):
    with open(filepath, 'r') as f:
        content = f.readlines()

    map_start_pattern = re.compile(r'val (ZhTwUiTexts|CantoneseUiTexts) = mapOf<UiTextKey, String>\(')
    insertion_index = -1

    for i, line in enumerate(content):
        if map_start_pattern.search(line):
            for j in range(i + 1, len(content)):
                if content[j].strip() == ')':
                    insertion_index = j
                    break
            break

    if insertion_index != -1:
        # Ensure the previous line ends with a comma if it's not a comment or brace
        if not content[insertion_index - 1].strip().endswith(',') and not content[insertion_index - 1].strip().endswith('(') and not content[insertion_index - 1].strip().startswith('//') and not content[insertion_index - 1].strip().startswith('/*'):
            content[insertion_index - 1] = content[insertion_index - 1].rstrip() + ',
'
        content.insert(insertion_index, f'    UiTextKey.{key_name} to "TODO: Translate {key_name}",
')
    else:
        print(f"Error: Could not find suitable insertion point in {filepath}")
        return False

    with open(filepath, 'w') as f:
        f.writelines(content)
    return True

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python3 add_translation_key.py <KEY_NAME> \"<ENGLISH_VALUE>\"")
        sys.exit(1)

    key_name = sys.argv[1]
    english_value = sys.argv[2]

    base_path = "FYP/app/src/main/java/com/example/fyp/model/ui/strings"
    uitext_core_path = f"{base_path}/UiTextCore.kt"
    uitext_screens_path = f"{base_path}/UiTextScreens.kt"
    zhtw_uitexts_path = f"{base_path}/translations/ZhTwUiTexts.kt"
    cantonese_uitexts_path = f"{base_path}/translations/CantoneseUiTexts.kt"

    print(f"Adding key '{key_name}' with English value '{english_value}'...")

    if not add_key_to_enum(uitext_core_path, key_name):
        sys.exit(1)
    print(f"Successfully added '{key_name}' to {uitext_core_path}")

    if not add_english_text(uitext_screens_path, key_name, english_value):
        sys.exit(1)
    print(f"Successfully added English text for '{key_name}' to {uitext_screens_path}")

    if not add_translated_text(zhtw_uitexts_path, key_name):
        sys.exit(1)
    print(f"Successfully added placeholder for '{key_name}' to {zhtw_uitexts_path}")

    if not add_translated_text(cantonese_uitexts_path, key_name):
        sys.exit(1)
    print(f"Successfully added placeholder for '{key_name}' to {cantonese_uitexts_path}")

    print("Translation key added successfully!")
