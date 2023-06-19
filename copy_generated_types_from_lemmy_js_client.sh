#!/bin/bash

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
  
  # Add = null to any lines containing ?
  sed -i '/\?/ s/$/ = null/' "$kt_file"

  # Change these interfaces to data classes
  sed -i 's/interface /data class /g' "$kt_file"
  sed -i 's/ {/(/g' "$kt_file"
  sed -i 's/}/)/g' "$kt_file"
  sed -i '/:/ s/$/,/' "$kt_file"

done

# Remove weird lib.es files
rm lib.*
rm Sensitive.kt
rm others.kt

# Remove all these weird module files
rm *.module_lemmy-js-client.kt

# Move all the kotlin types to our folder
mv *.kt ~/git/jerboa/app/src/main/java/com/jerboa/datatypes/types/

