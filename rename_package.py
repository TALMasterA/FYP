#!/usr/bin/env python3
"""
UTF-8-safe package rename: com.example.fyp -> com.translator.TalknLearn
Also renames source directories.
"""
import os
import shutil

OLD_PKG = "com.example.fyp"
NEW_PKG = "com.translator.TalknLearn"
OLD_PATH = "com/example/fyp"
NEW_PATH = "com/translator/TalknLearn"

SRC_BASES = [
    "app/src/main/java",
    "app/src/test/java",
    "app/src/androidTest/java",
    "app/src/debug/java",
]

ROOT = os.path.dirname(os.path.abspath(__file__))

def replace_in_file(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()
    new_content = content.replace(OLD_PKG, NEW_PKG).replace(OLD_PATH, NEW_PATH)
    if new_content != content:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(new_content)
        return True
    return False

def rename_dirs():
    for base in SRC_BASES:
        old_dir = os.path.join(ROOT, base, OLD_PATH)
        new_dir = os.path.join(ROOT, base, NEW_PATH)
        if os.path.isdir(old_dir):
            os.makedirs(os.path.dirname(new_dir), exist_ok=True)
            shutil.move(old_dir, new_dir)
            print(f"Moved: {old_dir} -> {new_dir}")
        else:
            print(f"Not found (skip): {old_dir}")

def process_kt_files():
    updated = 0
    skipped = 0
    for base in SRC_BASES:
        base_path = os.path.join(ROOT, base, NEW_PATH)
        if not os.path.isdir(base_path):
            continue
        for dirpath, _, filenames in os.walk(base_path):
            for fname in filenames:
                if fname.endswith(".kt"):
                    fp = os.path.join(dirpath, fname)
                    if replace_in_file(fp):
                        updated += 1
                    else:
                        skipped += 1
    print(f"KT files updated: {updated}, unchanged: {skipped}")

def verify_no_old_pkg():
    found = []
    for base in SRC_BASES:
        base_path = os.path.join(ROOT, base)
        if not os.path.isdir(base_path):
            continue
        for dirpath, _, filenames in os.walk(base_path):
            for fname in filenames:
                if fname.endswith(".kt"):
                    fp = os.path.join(dirpath, fname)
                    with open(fp, "r", encoding="utf-8") as f:
                        content = f.read()
                    if OLD_PKG in content or OLD_PATH in content:
                        found.append(fp)
    if found:
        print(f"WARNING: {len(found)} files still contain old package:")
        for f in found:
            print(f"  {f}")
    else:
        print("VERIFY OK: No old package references found in .kt files")

if __name__ == "__main__":
    print("Step 1: Rename directories...")
    rename_dirs()
    print("\nStep 2: Update package declarations in .kt files...")
    process_kt_files()
    print("\nStep 3: Verify...")
    verify_no_old_pkg()
    print("\nDone.")
