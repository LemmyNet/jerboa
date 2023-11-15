#!/bin/bash

O_CWD=$PWD

# Go to the types folder
cd ~/git/lemmy-js-client/src/types

# Run dukat
dukat *.ts

# Loop over every file 
for filename in *.ts; 
  do echo "Converting ${filename} ..."; 

  # Rename the file to a simple .kt
  file_without_ext=${filename%%.*}
  kt_file="${file_without_ext}.kt"

  # Skip files that don't exist
  if [ ! -f "${file_without_ext}.module_lemmy-js-client.kt" ]; then
     continue
  fi


  mv "${file_without_ext}.module_lemmy-js-client.kt" "$kt_file"

  # Remove all these weird dukat imports
  sed -i '1,15d' "$kt_file"

  # Remove all these weird getter and setter lines
  sed -i '/definedExternally/d' "$kt_file"

  # Add the package line
  sed -i '1ipackage com.jerboa.datatypes.types' "$kt_file" 

  # Change Number or Any to Int
  sed -i 's/Any\b/Int/g' "$kt_file"
  sed -i 's/Number\b/Int/g' "$kt_file"

  # Change Array to immutable List
  sed -i 's/Array\b/List/g' "$kt_file"

  # Change mutable var to immutable val
  sed -i 's/var\b/val/g' "$kt_file"

  # Convert a few string to enum types like sort, listing_type, etc
  sed -i 's/listing_type: String/listing_type: ListingType/g' "$kt_file"
  # These could also be SearchType, ModlogActionType
  sed -i 's/type_: String/type_: ListingType/g' "$kt_file"
  sed -i 's/subscribed: String/subscribed: SubscribedType/g' "$kt_file"
  # These could also be CommentSortType
  sed -i 's/sort: String/sort: SortType/g' "$kt_file"
  sed -i 's/sort_type: String/sort_type: SortType/g' "$kt_file"
  sed -i 's/registration_mode: String/registration_mode: RegistrationMode/g' "$kt_file"
  sed -i 's/feature_type: String/feature_type: PostFeatureType/g' "$kt_file"
  
  # Add = null to any lines containing ? if it is not a typealias
  if ! grep -q "typealias" "$kt_file"; then
      sed -i '/\?/ s/$/ = null/' "$kt_file"
  fi

  # Change these interfaces to data classes
  sed -i 's/interface /data class /g' "$kt_file"
  sed -i 's/ {/(/g' "$kt_file"
  sed -i 's/}/)/g' "$kt_file"
  sed -i '/:/ s/$/,/' "$kt_file"

  # Add @Parcelize annotation
  sed -i 's/data class/import android.os.Parcelable\nimport kotlinx.parcelize.Parcelize\n\n@Parcelize\ndata class/g' "$kt_file"

  # Add Parcelable inheritance
  if grep -q "data class" "$kt_file"; then
      echo " : Parcelable" >> "$kt_file"
  fi

  # Adds @Immutable annotation to classes containing lists, to mark them stable
  if grep -q "List<" "$kt_file"; then
    sed -i 's/@Parcelize/@Immutable\n@Parcelize/g' "$kt_file"
    sed -i 's/import android.os.Parcelable/import android.os.Parcelable\nimport androidx.compose.runtime.Immutable/g' "$kt_file"
  fi

  # These must be CommentSortType
  if [[ "$kt_file" =~ ^(GetComments|GetPersonMentions|GetReplies)\.kt$ ]]; then
      sed -i 's/sort: SortType/sort: CommentSortType/g' "$kt_file"
  fi

  # These must be SearchType
  if [[ "$kt_file" =~ ^(SearchResponse|Search)\.kt$ ]]; then
      sed -i 's/type_: ListingType/type_: SearchType/g' "$kt_file"
  fi

  #  File must end with a newline
   sed -i -e '$a\' "$kt_file"

done

# Remove weird lib.es files
rm lib.*

# Remove all these weird module files
rm *.module_lemmy-js-client.kt

# Move all the kotlin types to our folder
mv *.kt "$O_CWD/app/src/main/java/com/jerboa/datatypes/types/"

