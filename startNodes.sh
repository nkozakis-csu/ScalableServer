DIR="$( cd "$( dirname "$0" )" &&pwd )"
JAR_PATH="$DIR/build/libs/ScalableServer-1.0.jar"
MACHINE_LIST="$DIR/machine_list"
SCRIPT="java -cp $JAR_PATH cs455.scaling.client.Client 129.82.44.156 50000 3"
COMMAND='gnome-terminal --geometry=200x40'
for machine in `cat $MACHINE_LIST`
do
    OPTION='--tab -t "'$machine'" -e "ssh -t '$machine' cd '$DIR'; echo '$SCRIPT'; '$SCRIPT'"'
    COMMAND+=" $OPTION"
done
eval $COMMAND &