#!/bin/bash

# Script for building an executable from one or more assembly language
# files and object files.  The name of the first file, without its
# exetension, becomes the name of the executable.  All files must have
# a .o or .asm extension.  The command can start with options -c and/or -n.
# If option -c is given, then the files are linked with the standard C library
# and the standard C math library.  If the option -n is given, then the
# nasm assembler is used instead of the default yasm assembler.

CLIB=""
LINKC=""
ASM="yasm"
ASMCMD="yasm -felf64 -gdwarf2"


while [ -n "$1" -a "${1:0:1}" = "-" ] ; do
   if [ "$1" == "-c" ] ; then
      CLIB="-lc -lm --dynamic-linker /lib/x86_64-linux-gnu/ld-linux-x86-64.so.2"
      LINKC=" to C library"
   elif [ "$1" == "-n" ] ; then
      ASM="nasm"
      ASMCMD="nasm -felf64 -gdwarf"
   else
      echo "Unknown option '$1'.  The only options are -c to link"
      echo "to the C library and -n to use nasm instead of yasm."
      echo "Exiting with error"
      exit 1
   fi
   shift
done

if (( $# == 0 )) ; then
   echo "No file names provided for command."
   echo "This script requires one or more file names ending with .asm or .o"
   echo "Exiting with error"
   exit 1
fi

for file in $* ; do
   if [ "${file##*.}" != "asm" -a "${file##*.}" != "o" ] ; then
       echo "Unrecognized file type for $file"
       echo "All file names must end with .asm or .o"
       echo "Exiting with error"
       exit 1
   fi
done


ldfiles=""
first=""
for file in $* ; do
   
   if [ "${file%\.asm}" != "$file" ] ; then
      echo
      echo "Apply $ASM to $file"
      if $ASMCMD "$file" ; then
         echo "   ok"
      else
         echo
         echo "Errors in assembly language program $file"
         echo "Exiting with error"
         exit 1
      fi
      ldfiles="$ldfiles ${file%\.asm}.o"
      if [ -z "$first" ] ; then
         first="${file%\.asm}" 
      fi
   else
      ldfiles="$ldfiles $file"
      if [ -z "$first" ] ; then
         first="${file%\.o}" 
      fi
   fi
done

echo
echo "Linking Files$LINKC with ld"

if ld -g -o $first $ldfiles $CLIB; then
   echo "   Done.  Created executable program \"$first\""
   exit 0
fi

echo
echo "Errors encountered during linking."
echo "Exiting with error"
exit 1


