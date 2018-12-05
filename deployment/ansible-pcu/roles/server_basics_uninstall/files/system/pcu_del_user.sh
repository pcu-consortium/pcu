#!/bin/bash

pcu_user_to_del=$1
pcu_current_user=$(whoami)

if [[ "${pcu_current_user}" != "root" ]]
then
  echo "need to be root to lauch this script"
  exit 1
fi
pcu_user_id=$(id -u ${pcu_user_to_del} 2> /dev/null)
if [[ $? -eq 1 ]]
then
  echo "the user '${pcu_user_to_del}' does not exists"
  exit 1
fi

# locking account
passwd -l ${pcu_user_to_del}

# killing user's running process
pcu_count_process=$(pgrep -cu ${pcu_user_to_del})
if [[ ${pcu_count_process} -gt 0 ]]
then
  echo "running process found for this user -> kill all"
  killall -KILL -u ${pcu_user_to_del}
else
  echo "no runnging process for this user"
fi

# deleting crontab for this user
crontab -r -u ${pcu_user_to_del} 2> /dev/null

# finally delete this user
userdel -r ${pcu_user_to_del}  2> /dev/null
exit $?
