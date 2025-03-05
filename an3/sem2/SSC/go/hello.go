package main

import "fmt"

func main() {

	var student1 string = "John" //type is string
	var student2 = "Jane"        //type is inferred
	x := 2                       //type is inferred

	fmt.Println("Student 1: ", student1, "Student 2: ", student2, "x: ", x)
}
