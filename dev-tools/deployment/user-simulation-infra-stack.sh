#!/usr/bin/expect -f

set timeout 3

spawn "./incept-infra-stack.sh" "dummy"
expect "Enter the enviroment :"
send -- "dev\r"

expect "Enter the password :"
send -- "1234qwer\r"

expect eof
