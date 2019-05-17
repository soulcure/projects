package charge

type User struct{
	Account  string
	Money   int
}
type UserManager  map[string]*User

var Accounts UserManager
func init(){
	accounts:=make(map[string]*User)
	Accounts=accounts
}
func (m UserManager)AddUser(u *User){
	m[u.Account]=u
}

func (m UserManager)GetUser(account string)*User{
	if u,ok:=m[account];ok{
		return u
	}else{
		return nil
	}
}

func (m UserManager)DeleteUser(account string){
	delete(m,account)
}

