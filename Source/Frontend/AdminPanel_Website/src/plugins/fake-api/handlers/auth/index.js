import { HttpResponse, http } from 'msw'
import { db } from '@db/auth/db'

// Handlers for auth
export const handlerAuth = [
  http.post(('/api/auth/login'), async ({ request }) => {
    const { email, password } = await request.json()
    let errors = {
      email: ['Something went wrong'],
    }
    const user = db.users.find(u => u.email === email && u.password === password)
    if (user) {
      try {
        const accessToken = db.userTokens[user.id]

        // We are duplicating user here
        const userData = { ...user }

        const userOutData = Object.fromEntries(Object.entries(userData)
          .filter(([key, _]) => !(key === 'password' || key === 'abilityRules')))

        const response = {
          userAbilityRules: userData.abilityRules,
          accessToken,
          userData: userOutData,
        }

        return HttpResponse.json(response, { status: 201 })
      }
      catch (e) {
        errors = { email: [e] }
      }
    }
    else {
      errors = { email: ['Invalid email or password'] }
    }
    
    return HttpResponse.json({ errors }, { status: 400 })
  }),
]
