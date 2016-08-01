#!/bin/bash
if [ $# -ne 1 ]; then
echo "Please select a command."
else
 case "$1" in
        start)
		nohup java -jar jetty-runner.jar --port 8888 mca-checkout-0.1.0 &
		echo $! &> mca-checkout.pid
        echo "Starting server on port 8888"
        ;;
		
        stop)
				cat mca-checkout.pid | xargs -i kill -9 {}
				rm -rf mca-checkout.pid
                
        ;;
		
        *)
                echo "$1 command is not available"
        ;;
 esac
fi