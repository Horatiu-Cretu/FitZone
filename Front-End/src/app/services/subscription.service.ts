import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SubscriptionPlanViewDTO {
  id: number;
  name: string;
  description: string;
  price: number;
  durationDays: number;
  isActive: boolean;
  features?: string[];
  highlight?: boolean;
}

export interface PurchaseSubscriptionRequestDTO {
  planId: number | string;
  paymentConfirmationId?: string;
}

export interface UserSubscriptionViewDTO {
  id: number;
  userId: number;
  planName: string;
  planDescription: string;
  startDate: string;
  endDate: string;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'PENDING_PAYMENT';
  pricePaid: number;
  canCancel?: boolean;
  nextBillingDate?: string;
}

export interface SubscriptionPlanDTO {
  name: string;
  description: string;
  price: number;
  durationDays: number;
  isActive?: boolean;
}


@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private m3ProxyUrl = `${environment.apiUrl}/m3-proxy`;

  constructor(private http: HttpClient) { }

  getAllSubscriptionPlansForClient(): Observable<SubscriptionPlanViewDTO[]> {
    console.log('[SubscriptionService] getAllSubscriptionPlansForClient(): Fetching plans for client from URL:', `${this.m3ProxyUrl}/subscription-plans`);
    return this.http.get<SubscriptionPlanViewDTO[]>(`${this.m3ProxyUrl}/subscription-plans`);
  }

  purchaseSubscription(purchaseData: PurchaseSubscriptionRequestDTO): Observable<UserSubscriptionViewDTO> {
    return this.http.post<UserSubscriptionViewDTO>(`${this.m3ProxyUrl}/subscriptions/purchase`, purchaseData);
  }

  getMySubscription(): Observable<UserSubscriptionViewDTO> {
    return this.http.get<UserSubscriptionViewDTO>(`${this.m3ProxyUrl}/subscriptions/my-subscription`);
  }

  cancelMySubscription(): Observable<UserSubscriptionViewDTO> {
    return this.http.post<UserSubscriptionViewDTO>(`${this.m3ProxyUrl}/subscriptions/cancel`, {});
  }

  getAllSubscriptionPlansForAdmin(): Observable<SubscriptionPlanViewDTO[]> {
    console.log('[SubscriptionService] getAllSubscriptionPlansForAdmin(): Fetching plans for admin from URL:', `${this.m3ProxyUrl}/subscription-plans`); // THIS IS THE FAILING ONE
    return this.http.get<SubscriptionPlanViewDTO[]>(`${this.m3ProxyUrl}/subscription-plans`);
  }

  getSubscriptionPlanByIdForAdmin(planId: number | string): Observable<SubscriptionPlanViewDTO> {
    console.log(`[SubscriptionService] getSubscriptionPlanByIdForAdmin(${planId}): Fetching plan for admin.`);
    return this.http.get<SubscriptionPlanViewDTO>(`${this.m3ProxyUrl}/subscription-plans/${planId}`);
  }

  createAdminSubscriptionPlan(planData: SubscriptionPlanDTO): Observable<SubscriptionPlanViewDTO> {
    console.log('[SubscriptionService] createAdminSubscriptionPlan(): Creating plan for admin with data:', planData);
    return this.http.post<SubscriptionPlanViewDTO>(`${this.m3ProxyUrl}/admin/subscription-plans`, planData);
  }

  updateAdminSubscriptionPlan(planId: number | string, planData: SubscriptionPlanDTO): Observable<SubscriptionPlanViewDTO> {
    console.log(`[SubscriptionService] updateAdminSubscriptionPlan(${planId}): Updating plan for admin with data:`, planData);
    return this.http.put<SubscriptionPlanViewDTO>(`${this.m3ProxyUrl}/admin/subscription-plans/${planId}`, planData);
  }

  deleteAdminSubscriptionPlan(planId: number | string): Observable<void> {
    console.log(`[SubscriptionService] deleteAdminSubscriptionPlan(${planId}): Deleting plan for admin.`);
    return this.http.delete<void>(`${this.m3ProxyUrl}/admin/subscription-plans/${planId}`);
  }
}
