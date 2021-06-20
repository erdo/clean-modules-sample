#!/bin/bash

#
# this has only been lightly tested on a mac, it recursively renames
# directories and edits files, only use this if you've read the script
# and you understand what it is doing!
#
# chmod u+x change_package.sh
# ./change_package.sh -p com.mydomain.myapp
#

set -e;

print_usage() {
  echo -e "\033[31;musage:\033[0;m ./change_package.sh -p com.mydomain.myapp"
}

check_one_parameter() {
  if [ -z $1 ]; then
      echo -e "package must be in this format: x.y.z"
      print_usage ;
      exit 1
  fi
}

validate_alpha_only(){
  if [[ "$1" =~ [^a-z] ]]; then
    echo "$1 can only contain letters a-z"
    print_usage
    exit 1
  fi
}

tokenize_to_3_components() {
  IFS_BAK=$IFS #IFS is usually " \t\n" (those are backslashes not pipes) we want to put it back after changing it
  IFS='.'

  tokens=( $1 )

  NEW_PACKAGE_0=${tokens[0]}
  NEW_PACKAGE_1=${tokens[1]}
  NEW_PACKAGE_2=${tokens[2]}

  IFS=$IFS_BAK

  check_one_parameter $NEW_PACKAGE_0
  check_one_parameter $NEW_PACKAGE_1
  check_one_parameter $NEW_PACKAGE_2

  validate_alpha_only $NEW_PACKAGE_0
  validate_alpha_only $NEW_PACKAGE_1
  validate_alpha_only $NEW_PACKAGE_2

  if [ ${#tokens[@]} -gt 3 ]; then
      echo "package cannot have more than 3 components"
      print_usage ;
      exit 1
  fi

  if [[ "$1" == *. ]]; then
    echo "package cannot end with a ."
    print_usage ;
    exit 1
  fi
}

confirm_continue() {
  while true
  do
      read -r -p 'Warning, this recursively edits files and renames directories! Only use this if you have read the script and you understand it. Do you want to continue? ' choice
      case "$choice" in
        n|N) echo 'aborted'; exit 1;;
        y|Y) break ;;
        *) echo 'please type Y or N';;
      esac
  done
}

edit_files() {
  if [ "$1" != "$2" ]; then
    # file contents
    git grep -I -l "$1" -- './*' ':!*.sh' ':!*.md' ':!/build.gradle' | xargs sed -i "" -e "s/$1/$2/g"
  fi
}

rename_dirs() {
  if [ "$1" != "$2" ]; then
    # directory names
    find . -type d -depth -name "$1" -execdir sh -c 'mv {} '"$2" \;
  fi
}

while getopts 'p:' flag; do
  case "${flag}" in
    p) NEW_PACKAGE="${OPTARG}" ;;
    *) print_usage ; exit 1 ;;
  esac
done

check_one_parameter $NEW_PACKAGE

tokenize_to_3_components $NEW_PACKAGE

echo "changing package to $NEW_PACKAGE"

confirm_continue

edit_files "foo" $NEW_PACKAGE_0
edit_files "bar" $NEW_PACKAGE_1
edit_files "clean" $NEW_PACKAGE_2

rename_dirs "foo" $NEW_PACKAGE_0
rename_dirs "bar" $NEW_PACKAGE_1
rename_dirs "clean" $NEW_PACKAGE_2

echo -e "package rename complete, please verify and commit the changes"
