import { TestBed } from '@angular/core/testing';

import { TokenService } from './token.service';

describe('TokenService', () => {
  let service: TokenService;

  const mockGetItem = jasmine.createSpy('getItem');
  const mockSetItem = jasmine.createSpy('setItem');
  const mockRemoveItem = jasmine.createSpy('removeItem');
  const localStorageOrig = window.localStorage;
  const localStorageMock = {
    value: {
      getItem: mockGetItem,
      setItem: mockSetItem,
      removeItem: mockRemoveItem,
    },
  };

  beforeEach(() => {
    Object.defineProperty(window, 'localStorage', localStorageMock);
    TestBed.configureTestingModule({
      providers: [],
    });
    service = TestBed.inject(TokenService);
  });

  afterEach(() => {
    Object.defineProperty(window, 'localStorage', localStorageOrig);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set and get token', () => {
    mockSetItem.and.stub();
    service.token = '123';
    expect(mockSetItem).toHaveBeenCalledTimes(1);
    expect(mockSetItem).toHaveBeenCalledWith('token', '123');

    mockGetItem.and.returnValue('123');
    const token = service.token;
    expect(mockGetItem).toHaveBeenCalledTimes(1);
    expect(mockGetItem).toHaveBeenCalledWith('token');
    expect(token).toEqual('123');
    mockGetItem.and.stub();
  });
});
