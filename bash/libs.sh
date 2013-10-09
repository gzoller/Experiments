#!/bin/bash

origIFS="$IFS"

replace() {
	local tagStr=$1
	local replStr=$2
	local srcFile=$3
	perl -pi -e"s/$tagStr/$replStr/" $srcFile
	#perl -pi -e"s/$tagStr/$replStr/e" $srcFile
}

getMyIP() {
	local sys=`uname -s`
	if [ "$sys" = "Darwin" ]; then  # OSX
		IP=`ifconfig | grep "inet " | grep -v 127.0.0.1 | cut -f2 -d\ | head -1`
	else  # Linux and hopefully everything else!
		IP=`hostname --ip-address`
	fi
}

commaQuoteList() {
	local list=("$@")
	local first=false
	LIST=""
	for item in "${list[@]}"
	do
		if $first = false; then
			LIST="$LIST,"
		fi
		first=true
		LIST="$LIST\"$item\""
	done
}

isAWS() {
	if ( `command ec2-describe-tags >/dev/null 2>&1` ); then
		return 0
	else
		return 1
	fi
}

ask() {
	local promptStr=$1
	shift
	local okResp=("$@") # array of acceptable responses
	done=
	IFS=","
	ansJoin="${okResp[*]}"
	IFS=$origIFS
	while [ -z "$done" ]; do
		show "$promptStr"
		show ":B:($ansJoin)|N|: "
		read -p "" reply
		read -a testAns <<< "${okResp[@]//$reply/}"
		if [ "${#testAns[@]}" != "${#okResp[@]}" ]; then
			ANSWER="$reply"
			done=1
		fi
	done
}

_boldTag=":B:"
_ulTag=":U:"
_dimTag="|D|"
_redTag="|R|"
_greenTag="|G|"
_yellowTag="|Y|"
_blueTag="|B|"
_pinkTag="|P|"
_whiteTag="|W|" # "super-white"
_normalTag="|N|"
_bold="$(tput bold)"
_ul="$(tput smul)"
_dim="$(tput setaf 0)"
_red="$(tput setaf 1)"
_green="$(tput setaf 2)"
_yellow="$(tput setaf 3)"
_blue="$(tput setaf 4)"
_pink="$(tput setaf 5)"
_white="$(tput setaf 7)"
_normal="$(tput setaf 9)$(tput sgr0)$(tput rmul)"

show() {
	local text=$1
	IFS=
	text=`echo $text | sed -e "s/$_boldTag/$_bold/g"`
	text=`echo $text | sed -e "s/$_ulTag/$_ul/g"`
	text=`echo $text | sed -e "s/$_dimTag/$_dim/g"`
	text=`echo $text | sed -e "s/$_redTag/$_red/g"`
	text=`echo $text | sed -e "s/$_greenTag/$_green/g"`
	text=`echo $text | sed -e "s/$_yellowTag/$_yellow/g"`
	text=`echo $text | sed -e "s/$_blueTag/$_blue/g"`
	text=`echo $text | sed -e "s/$_pinkTag/$_pink/g"`
	text=`echo $text | sed -e "s/$_whiteTag/$_white/g"`
	text=`echo $text | sed -e "s/$_normalTag/$_normal/g"`
	printf "$text"
}

showFile() {
	local file=$1
	IFS= 
	while read -r line
	do
		show "$line"
		printf "\n"
	done < $file
	IFS=$origIFS
}

info() {
	show "|Y|[ Info ]|N| $1\n"
}
query() {
	local promptStr=$1
	shift
	local okResp=("$@") # array of acceptable responses
	local p="|B|Question> |N|$promptStr "
	if [ -z "$okResp" ]; then # open-ended query
		show "$p"
		read -p "" ANSWER
	else  # bound query
		ask "$p" "${okResp[@]}"
	fi
}
#alert() {
#        printf "$(tput setaf 1)$1$(tput setaf 9)\n"
#}
