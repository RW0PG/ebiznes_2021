export interface UserDb {
    id: number,
    email: string,
    SignInDetails: {
        providerId: string,
        providerKey: string,
    },
}
